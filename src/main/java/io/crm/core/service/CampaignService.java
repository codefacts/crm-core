package io.crm.core.service;

import io.crm.Events;
import io.crm.core.App;
import io.crm.core.model.Campaign;
import io.crm.core.model.Query;
import io.crm.core.model.User;
import io.crm.mc;
import io.crm.util.ErrorBuilder;
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
import static io.crm.core.model.Query.id;
import static io.crm.core.model.Query.userId;
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
            dbService.validateBrandId(Util.id(campaign.getValue(Query.brand)), brandValidation -> {

                if (brandValidation != null) {
                    errorBuilder.put(Query.brand, brandValidation);
                }

                final String launchDate = campaign.getValue(Campaign.launchDate) instanceof String ? campaign.getString(Campaign.launchDate)
                        : campaign.getJsonObject(Campaign.launchDate, new JsonObject()).getString(Query.$date);
                final String closeDate = campaign.getValue(Campaign.closeDate) instanceof String ? campaign.getString(Campaign.closeDate)
                        : campaign.getJsonObject(Campaign.closeDate, new JsonObject()).getString(Query.$date);
                final String salaryStartDate = campaign.getValue(Campaign.salaryStartDate) instanceof String ? campaign.getString(Campaign.salaryStartDate)
                        : campaign.getJsonObject(Campaign.salaryStartDate, new JsonObject()).getString(Query.$date);
                final String salaryEndDate = campaign.getValue(Campaign.salaryEndDate) instanceof String ? campaign.getString(Campaign.salaryEndDate)
                        : campaign.getJsonObject(Campaign.salaryEndDate, new JsonObject()).getString(Query.$date);

                checkRequired(errorBuilder, launchDate, "launchDate", "Launch Date is required");
                checkRequired(errorBuilder, closeDate, "closeDate", "Close Date is required");
                checkRequired(errorBuilder, salaryStartDate, "salaryStartDate", "Salary Start Date is required");
                checkRequired(errorBuilder, salaryEndDate, "salaryEndDate", "Salary End Date is required");

                validateDateFormat(errorBuilder, launchDate, "launchDate", "Launch Date is invalid.");
                validateDateFormat(errorBuilder, closeDate, "closeDate", "Close Date is invalid.");
                validateDateFormat(errorBuilder, salaryStartDate, "salaryStartDate", "Salary Start Date is invalid.");
                validateDateFormat(errorBuilder, salaryEndDate, "salaryEndDate", "Salary End Date is invalid.");

                validateTree(campaign.getJsonObject(Query.tree, new JsonObject()), (tree, eb) -> {

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

    private void validateTree(final JsonObject tree, final BiConsumer<JsonObject, ErrorBuilder> biConsumer, final Message message) {
        final ErrorBuilder errorBuilder = new ErrorBuilder();
        final List<JsonObject> regionList = tree.getMap().values().stream().map(v -> toJsonObject(v)).collect(Collectors.toList());

        app.getMongoClient().find(mc.regions.name(),
                new JsonObject()
                        .put(Query.id, new JsonObject()
                                .put(Query.$in, regionList.stream().map(v -> v.getLong(Query.id)).collect(Collectors.toList()))),
                withReply(rList -> {

                    if (rList.size() < regionList.size()) {
                        final Set<Long> rListIdSet = idSet(rList);
                        putInErrorBuilder(errorBuilder, regionList, rListIdSet, Query.regionId, "Regions");
                        return;
                    }

                    final Map<Long, JsonObject> areaList = collect(regionList, mc.areas.name());

                    app.getMongoClient().find(mc.areas.name(),
                            new JsonObject()
                                    .put(Query.id, new JsonObject()
                                            .put(Query.$in, new JsonArray(areaList.values().stream().map(v -> v.getLong(Query.id)).collect(Collectors.toList())))),
                            withReply(aList -> {
                                if (aList.size() < areaList.size()) {
                                    final Set<Long> aListIdSet = idSet(aList);
                                    putInErrorBuilder(errorBuilder, areaList.values(), aListIdSet, Query.areaId, "Areas");
                                    return;
                                }

                                checkParent(aList, areaList, errorBuilder, Query.region, Query.area, Query.areaRegionId);

                                Map<Long, JsonObject> houseList = collect(areaList.values(), mc.distribution_houses.name());

                                app.getMongoClient().find(mc.distribution_houses.name(),
                                        new JsonObject()
                                                .put(Query.id, new JsonObject()
                                                        .put(Query.$in, new JsonArray(houseList.values().stream().map(v -> v.getLong(Query.id)).collect(Collectors.toList())))),
                                        withReply(hList -> {

                                            if (hList.size() < houseList.size()) {
                                                final Set<Long> hListIdSet = idSet(hList);
                                                putInErrorBuilder(errorBuilder, houseList.values(), hListIdSet, Query.houseId, "Houses");
                                                return;
                                            }

                                            checkParent(hList, houseList, errorBuilder, Query.area, Query.distributionHouse, Query.distributionHouseAreaId);

                                            final Map<Long, JsonObject> locationList = collect(houseList.values(), mc.locations.name());

                                            app.getMongoClient().find(mc.locations.name(), new JsonObject()
                                                            .put(Query.id, new JsonObject()
                                                                    .put(Query.$in, new JsonArray(locationList.values().stream().map(v -> v.getLong(Query.id)).collect(Collectors.toList())))),
                                                    withReply(lList -> {
                                                        if (lList.size() < locationList.size()) {
                                                            final Set<Long> lListIdSet = idSet(lList);
                                                            putInErrorBuilder(errorBuilder, locationList.values(), lListIdSet, Query.locationId, "Locations");
                                                            return;
                                                        }

                                                        checkParent(lList, locationList, errorBuilder, Query.distributionHouse, Query.location, Query.locationDistributionHouseId);
                                                    }, message));

                                            final Map<String, JsonObject> brList = collectUser(houseList.values(), Query.brs);

                                            app.getMongoClient().find(mc.employees.name(), new JsonObject()
                                                            .put(Query.userId, new JsonObject()
                                                                    .put(Query.$in, new JsonArray(
                                                                            brList.values().stream()
                                                                                    .map(v -> v.getString(Query.userId))
                                                                                    .collect(Collectors.toList())))),
                                                    withReply(bList -> {
                                                        if (bList.size() < brList.size()) {
                                                            final Set<String> bListIdSet = userIdSet(bList);
                                                            putInErrorBuilderForUser(errorBuilder, brList.values(), bListIdSet, Query.brId, "BRS");
                                                            return;
                                                        }

                                                        checkParentForUser(bList, brList, errorBuilder, Query.distributionHouse, Query.br, Query.brDistributionHouseId);
                                                    }, message));

                                            final Map<String, JsonObject> brSupervisorList = collectUser(houseList.values(), Query.brSupervisors);

                                            app.getMongoClient().find(mc.employees.name(), new JsonObject()
                                                            .put(Query.userId, new JsonObject().put(Query.$in, new JsonArray(
                                                                    brSupervisorList.values().stream()
                                                                            .map(v -> v.getString(Query.userId))
                                                                            .collect(Collectors.toList())))),
                                                    withReply(supList -> {
                                                        if (supList.size() < brSupervisorList.size()) {
                                                            final Set<String> supListIdSet = userIdSet(supList);
                                                            putInErrorBuilderForUser(errorBuilder, brSupervisorList.values(), supListIdSet, Query.brSupervisorId, "BR Supervisors");
                                                            return;
                                                        }

                                                        checkParentForUser(supList, brSupervisorList, errorBuilder, Query.distributionHouse, Query.brSupervisor, Query.brSupervisorDistributionHouseId);
                                                    }, message));
                                        }, message));


                                final Map<String, JsonObject> areaCoordinatorList = collectUser(areaList.values(), Query.areaCoordinators);

                                app.getMongoClient().find(mc.employees.name(), new JsonObject(), withReply(acList -> {
                                    if (acList.size() < areaCoordinatorList.size()) {
                                        final Set<String> acListIdSet = userIdSet(acList);
                                        putInErrorBuilderForUser(errorBuilder, areaCoordinatorList.values(), acListIdSet, Query.areaCoordinatorId, "Area Coordinators");
                                        return;
                                    }

                                    checkParentForUser(acList, areaCoordinatorList, errorBuilder, Query.area, Query.areaCoordinator, Query.areaCoordinatorAreaId);
                                }, message));
                            }, message));
                }, message));
    }

    private void checkParentForUser(final List<JsonObject> aList, final Map<String, JsonObject> areaList, final ErrorBuilder errorBuilder, final String parent, final String child, final String errorField) {
        aList.forEach(user -> {
            if (!user.getJsonObject(parent).getString(Query.userId).equals(areaList.get(user.getString(Query.userId)).getJsonObject(parent).getString(Query.userId))) {
                errorBuilder.put(errorField, String.format("The " + parent + " ID %d for " + child + " id %d is incorrect.",
                        areaList.get(user.getString(Query.userId)).getJsonObject(parent).getString(Query.userId), user.getString(Query.userId)));
            }
        });
    }

    private void checkParent(final List<JsonObject> aList, final Map<Long, JsonObject> areaList, final ErrorBuilder errorBuilder, final String parent, final String child, final String field) {
        aList.forEach(area -> {
            if (!area.getJsonObject(parent).getLong(Query.id).equals(areaList.get(area.getLong(Query.id)).getJsonObject(parent).getLong(Query.id))) {
                errorBuilder.put(field, String.format("The " + parent + " ID %d for " + child + " id %d is incorrect.",
                        areaList.get(area.getLong(Query.id)).getJsonObject(parent).getLong(Query.id), area.getLong(Query.id)));
            }
        });
    }

    private Set<String> userIdSet(List<JsonObject> supList) {
        return supList.stream().map(v -> v.getString(User.userId)).collect(Collectors.toSet());
    }

    private Map<String, JsonObject> collectUser(final Collection<JsonObject> regionList, final String field) {
        return regionList
                .stream()
                .map(v -> v.getJsonObject(field).getMap().values())
                .flatMap(Collection::stream).map(v -> toJsonObject(v)).collect(Collectors.toMap(v -> v.getString(Query.userId), v -> v));
    }

    private Map<Long, JsonObject> collect(final Collection<JsonObject> regionList, final String field) {
        return regionList
                .stream()
                .map(v -> v.getJsonObject(field).getMap().values())
                .flatMap(Collection::stream).map(v -> toJsonObject(v)).collect(Collectors.toMap(v -> v.getLong(Query.id), v -> v));
    }

    private void putInErrorBuilderForUser(final ErrorBuilder errorBuilder, final Collection<JsonObject> regionList, final Set<String> rListIdSet, final String field, final String label) {
        errorBuilder.put(field, String.format(label + " % are invlid.", regionList.stream()
                .filter(v -> !rListIdSet.contains(v.getString(Query.userId))).map(v -> String.format("[UserID: %d, Name: %s]",
                        v.getString(Query.userId), v.getString(Query.name))).collect(Collectors.toList())));
    }

    private void putInErrorBuilder(final ErrorBuilder errorBuilder, final Collection<JsonObject> regionList, final Set<Long> rListIdSet, final String field, final String label) {
        errorBuilder.put(field, String.format(label + " % are invlid.", regionList.stream()
                .filter(v -> !rListIdSet.contains(v.getLong(Query.id))).map(v -> String.format("[ID: %d, Name: %s]",
                        v.getLong(Query.id), v.getString(Query.name))).collect(Collectors.toList())));
    }

    private Set<Long> idSet(List<JsonObject> rList) {
        return rList.stream().map(v -> v.getLong(Query.id)).collect(Collectors.toSet());
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
