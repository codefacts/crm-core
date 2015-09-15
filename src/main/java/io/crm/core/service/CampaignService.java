package io.crm.core.service;

import io.crm.Events;
import io.crm.QC;
import io.crm.core.App;
import io.crm.model.Campaign;
import io.crm.model.User;
import io.crm.mc;
import io.crm.util.ErrorBuilder;
import io.crm.util.TaskCoordinator;
import io.crm.util.TaskCoordinatorBuilder;
import io.crm.util.Util;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static io.crm.core.helper.Helper.checkRequired;
import static io.crm.core.helper.Helper.validateDateFormat;
import static io.crm.QC.id;
import static io.crm.util.ExceptionUtil.withReply;
import static io.crm.util.Util.*;

/**
 * Created by someone on 31/08/2015.
 */
@Component
public class CampaignService {
    private final App app;
    @Autowired
    private DbService dbService;

    @Autowired
    public CampaignService(App app) {
        this.app = app;
    }

    public void create(final Message<JsonObject> message) {
        final JsonObject campaign = message.body();
        final mc campaigns = mc.campaigns;
        dbService.validateIdAndNameOnCreate(campaign, obj -> {

            final ErrorBuilder errorBuilder = new ErrorBuilder();
            dbService.validateBrandId(Util.id(campaign.getValue(QC.brand)), brandValidation -> {

                if (brandValidation != null) {
                    errorBuilder.put(QC.brand, brandValidation);
                }

                final String launchDate = campaign.getValue(Campaign.launchDate) instanceof String ? campaign.getString(Campaign.launchDate)
                        : campaign.getJsonObject(Campaign.launchDate, new JsonObject()).getString(QC.$date);
                final String closeDate = campaign.getValue(Campaign.closeDate) instanceof String ? campaign.getString(Campaign.closeDate)
                        : campaign.getJsonObject(Campaign.closeDate, new JsonObject()).getString(QC.$date);
                final String salaryStartDate = campaign.getValue(Campaign.salaryStartDate) instanceof String ? campaign.getString(Campaign.salaryStartDate)
                        : campaign.getJsonObject(Campaign.salaryStartDate, new JsonObject()).getString(QC.$date);
                final String salaryEndDate = campaign.getValue(Campaign.salaryEndDate) instanceof String ? campaign.getString(Campaign.salaryEndDate)
                        : campaign.getJsonObject(Campaign.salaryEndDate, new JsonObject()).getString(QC.$date);

                checkRequired(errorBuilder, launchDate, "launchDate", "Launch Date is required");
                checkRequired(errorBuilder, closeDate, "closeDate", "Close Date is required");
                checkRequired(errorBuilder, salaryStartDate, "salaryStartDate", "Salary Start Date is required");
                checkRequired(errorBuilder, salaryEndDate, "salaryEndDate", "Salary End Date is required");

                validateDateFormat(errorBuilder, launchDate, "launchDate", "Launch Date is invalid.");
                validateDateFormat(errorBuilder, closeDate, "closeDate", "Close Date is invalid.");
                validateDateFormat(errorBuilder, salaryStartDate, "salaryStartDate", "Salary Start Date is invalid.");
                validateDateFormat(errorBuilder, salaryEndDate, "salaryEndDate", "Salary End Date is invalid.");

                validateTree(campaign.getJsonArray(QC.tree, new JsonArray()), (tree, eb) -> {
                    app.getBus().send(Events.VALIDATE_CAMPAIGN, campaign);
                }, message);

//                app.getMongoClient().insert(campaigns + "", campaign.put(id, campaigns.getNextId())
//                                .put(Campaign.brand, brandResutl.result()),
//                        withReply(rr -> {
//                            campaigns.incrementNextId();
//                            message.reply(null);
//                            app.getBus().publish(Events.NEW_CAMPAIGN_CREATED, campaign);
//                            System.out.println("CAMPAIGN CREATION SUCCESSFUL. CAMPAIGN: " + campaign);
//                        }, message));

            }, message);

        }, campaigns, message);
    }

    private void validateTree(final JsonArray tree, final BiConsumer<JsonArray, ErrorBuilder> biConsumer, final Message message) {
        final ErrorBuilder errorBuilder = new ErrorBuilder();
        final List<JsonObject> regionList = tree.stream().map(v -> toJsonObject(v)).collect(Collectors.toList());

        final TaskCoordinator taskCoordinator = new TaskCoordinatorBuilder()
                .count(7)
                .onSuccess(() -> {
                    biConsumer.accept(tree, errorBuilder);
                })
                .message(message)
                .get();

        app.getMongoClient().find(mc.regions.name(),
                new JsonObject()
                        .put(QC.id, new JsonObject()
                                .put(QC.$in, regionList.stream().map(v -> v.getLong(QC.id)).collect(Collectors.toList()))),
                taskCoordinator.add(rList -> {

                    if (rList.size() < regionList.size()) {
                        final Set<Long> rListIdSet = idSet(rList);
                        putInErrorBuilder(errorBuilder, regionList, rListIdSet, QC.regionId, "Regions");
                        return;
                    }

                    final Map<Long, JsonObject> areaList = collect(regionList, mc.areas.name(), QC.region);

                    app.getMongoClient().find(mc.areas.name(),
                            new JsonObject()
                                    .put(QC.id, new JsonObject()
                                            .put(QC.$in, new JsonArray(areaList.values().stream().map(v -> v.getLong(QC.id)).collect(Collectors.toList())))),
                            taskCoordinator.add(aList -> {
                                if (aList.size() < areaList.size()) {
                                    final Set<Long> aListIdSet = idSet(aList);
                                    putInErrorBuilder(errorBuilder, areaList.values(), aListIdSet, QC.areaId, "Areas");
                                    return;
                                }

                                checkParent(aList, areaList, errorBuilder, QC.region, QC.area, QC.areaRegionId);

                                Map<Long, JsonObject> houseList = collect(areaList.values(), mc.distributionHouses.name(), QC.area);

                                app.getMongoClient().find(mc.distributionHouses.name(),
                                        new JsonObject()
                                                .put(QC.id, new JsonObject()
                                                        .put(QC.$in, new JsonArray(houseList.values().stream().map(v -> v.getLong(QC.id)).collect(Collectors.toList())))),
                                        taskCoordinator.add(hList -> {

                                            if (hList.size() < houseList.size()) {
                                                final Set<Long> hListIdSet = idSet(hList);
                                                putInErrorBuilder(errorBuilder, houseList.values(), hListIdSet, QC.houseId, "Houses");
                                                return;
                                            }

                                            checkParent(hList, houseList, errorBuilder, QC.area, QC.distributionHouse, QC.distributionHouseAreaId);

                                            final Map<Long, JsonObject> locationList = collect(houseList.values(), mc.locations.name(), QC.distributionHouse);

                                            app.getMongoClient().find(mc.locations.name(), new JsonObject()
                                                            .put(QC.id, new JsonObject()
                                                                    .put(QC.$in, new JsonArray(locationList.values().stream().map(v -> v.getLong(QC.id)).collect(Collectors.toList())))),
                                                    taskCoordinator.add(lList -> {
                                                        if (lList.size() < locationList.size()) {
                                                            final Set<Long> lListIdSet = idSet(lList);
                                                            putInErrorBuilder(errorBuilder, locationList.values(), lListIdSet, QC.locationId, "Locations");
                                                            return;
                                                        }

                                                        checkParent(lList, locationList, errorBuilder, QC.distributionHouse, QC.location, QC.locationDistributionHouseId);
                                                    }));

                                            final Map<String, JsonObject> brList = collectUser(houseList.values(), QC.brs, QC.distributionHouse);

                                            app.getMongoClient().find(mc.employees.name(), new JsonObject()
                                                            .put(QC.userId, new JsonObject()
                                                                    .put(QC.$in, new JsonArray(
                                                                            brList.values().stream()
                                                                                    .map(v -> v.getString(QC.userId))
                                                                                    .collect(Collectors.toList())))),
                                                    taskCoordinator.add(bList -> {
                                                        if (bList.size() < brList.size()) {
                                                            final Set<String> bListIdSet = userIdSet(bList);
                                                            putInErrorBuilderForUser(errorBuilder, brList.values(), bListIdSet, QC.brId, "BRS");
                                                            return;
                                                        }

                                                        checkParentForUser(bList, brList, errorBuilder, QC.distributionHouse, QC.br, QC.brDistributionHouseId);
                                                    }));

                                            final Map<String, JsonObject> brSupervisorList = collectUser(houseList.values(), QC.brSupervisors, QC.distributionHouse);

                                            app.getMongoClient().find(mc.employees.name(), new JsonObject()
                                                            .put(QC.userId, new JsonObject().put(QC.$in, new JsonArray(
                                                                    brSupervisorList.values().stream()
                                                                            .map(v -> v.getString(QC.userId))
                                                                            .collect(Collectors.toList())))),
                                                    taskCoordinator.add(supList -> {
                                                        if (supList.size() < brSupervisorList.size()) {
                                                            final Set<String> supListIdSet = userIdSet(supList);
                                                            putInErrorBuilderForUser(errorBuilder, brSupervisorList.values(), supListIdSet, QC.brSupervisorId, "BR Supervisors");
                                                            return;
                                                        }

                                                        checkParentForUser(supList, brSupervisorList, errorBuilder, QC.distributionHouse, QC.brSupervisor, QC.brSupervisorDistributionHouseId);
                                                    }));
                                        }));


                                final Map<String, JsonObject> areaCoordinatorList = collectUser(areaList.values(), QC.areaCoordinators, QC.area);

                                app.getMongoClient().find(mc.employees.name(), new JsonObject()
                                                .put(QC.userId, new JsonObject().put(QC.$in, new JsonArray(
                                                        areaCoordinatorList.values().stream()
                                                                .map(v -> v.getString(QC.userId))
                                                                .collect(Collectors.toList())))),
                                        taskCoordinator.add(acList -> {
                                            if (acList.size() < areaCoordinatorList.size()) {
                                                final Set<String> acListIdSet = userIdSet(acList);
                                                putInErrorBuilderForUser(errorBuilder, areaCoordinatorList.values(), acListIdSet, QC.areaCoordinatorId, "Area Coordinators");
                                                return;
                                            }

                                            checkParentForUser(acList, areaCoordinatorList, errorBuilder, QC.area, QC.areaCoordinator, QC.areaCoordinatorAreaId);
                                        }));
                            }));
                }));
    }

    private void checkParentForUser(final List<JsonObject> aList, final Map<String, JsonObject> areaList, final ErrorBuilder errorBuilder, final String parent, final String child, final String errorField) {
        try {
            aList.forEach(user -> {
                if (!user.getJsonObject(parent).getLong(QC.id).equals(
                        areaList.get(user.getString(QC.userId))
                                .getJsonObject(parent).getLong(QC.id))) {
                    errorBuilder.put(errorField, String.format("The " + parent + " ID %d for " + child + " id %d is incorrect.",
                            areaList.get(user.getString(QC.userId))
                                    .getJsonObject(parent).getLong(QC.id), user.getString(QC.userId)));
                }
            });
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    private void checkParent(final List<JsonObject> aList, final Map<Long, JsonObject> areaList, final ErrorBuilder errorBuilder, final String parent, final String child, final String field) {
        try {
            aList.forEach(area -> {
                if (!area.getJsonObject(parent).getLong(QC.id).equals(
                        areaList.get(area.getLong(QC.id))
                                .getJsonObject(parent).getLong(QC.id))) {
                    errorBuilder.put(field, String.format("The " + parent + " ID %d for " + child + " id %d is incorrect.",
                            areaList.get(area.getLong(QC.id)).getJsonObject(parent).getLong(QC.id), area.getLong(QC.id)));
                }
            });
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    private Set<String> userIdSet(List<JsonObject> supList) {
        return supList.stream().map(v -> v.getString(User.userId)).collect(Collectors.toSet());
    }

    private Map<String, JsonObject> collectUser(final Collection<JsonObject> regionList, final String field, final String parentField) {
        Map<String, JsonObject> map = new HashMap<>();
        regionList.forEach(v -> {
            v.getJsonArray(field, new JsonArray()).forEach(a -> {
                final JsonObject jsonObject = toJsonObject(a);
                map.put(jsonObject.getString(QC.userId), jsonObject.put(parentField,
                        new JsonObject()
                                .put(QC.id, v.getLong(QC.id))));
            });
        });
        return map;
    }

    private Map<Long, JsonObject> collect(final Collection<JsonObject> regionList, final String field, final String parentField) {
        Map<Long, JsonObject> map = new HashMap<>();
        regionList.forEach(v -> {
            v.getJsonArray(field, new JsonArray()).forEach(a -> {
                final JsonObject jsonObject = toJsonObject(a);
                map.put(jsonObject.getLong(QC.id), jsonObject.put(parentField,
                        new JsonObject()
                                .put(QC.id, v.getLong(QC.id))));
            });
        });
        return map;
    }

    private void putInErrorBuilderForUser(final ErrorBuilder errorBuilder, final Collection<JsonObject> regionList, final Set<String> rListIdSet, final String field, final String label) {
        errorBuilder.put(field, String.format(label + " %s are invlid.",
                regionList.stream()
                        .filter(v -> !rListIdSet.contains(v.getString(QC.userId)))
                        .map(v -> String.format("[UserID: %d, Name: %s]",
                                v.getString(QC.userId), v.getString(QC.name)))
                        .collect(Collectors.toList())
        ));
    }

    private void putInErrorBuilder(final ErrorBuilder errorBuilder, final Collection<JsonObject> regionList, final Set<Long> rListIdSet, final String field, final String label) {
        errorBuilder.put(field, String.format(label + " %s are invlid.",
                regionList.stream()
                        .filter(v -> !rListIdSet.contains(v.getLong(QC.id)))
                        .map(v -> String.format("[ID: %d, Name: %s]",
                                v.getLong(QC.id), v.getString(QC.name)))
                        .collect(Collectors.toList())));
    }

    private Set<Long> idSet(List<JsonObject> rList) {
        return rList.stream().map(v -> v.getLong(QC.id)).collect(Collectors.toSet());
    }

    public void update(Message<JsonObject> message) {
        final JsonObject obj = message.body();
        final mc campaigns = mc.campaigns;
        dbService.validateIdAndNameOnEdit(obj, a -> {

            app.getMongoClient().update(campaigns + "", new JsonObject().put(id, obj.getLong(id)), updateObject(obj),
                    withReply(rr -> {
                        message.reply(null);
                        app.getBus().publish(Events.CAMPAIGN_UPDATED, obj);
                        System.out.println("CAMPAIGN UPDATE SUCCESSFUL. CAMPAIGN: " + obj);
                    }, message));

        }, campaigns, message);
    }
}
