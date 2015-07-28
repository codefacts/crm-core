package io.crmcore;

import io.crmcore.codec.*;
import io.crmcore.model.*;
import io.crmcore.service.*;
import io.vertx.core.*;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.ArrayList;

/**
 * Created by someone on 08-Jul-2015.
 */
public class MainVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        System.out.println("--------------Strating verticle");
        App.vertx = getVertx();
        App.bus = getVertx().eventBus();

        getVertx().executeBlocking((Future<ConfigurableApplicationContext> e) -> {

            final ConfigurableApplicationContext context = SpringApplication.run(App.class);
            e.complete(context);

        }, r -> {
            if (r.succeeded()) {
                final ConfigurableApplicationContext context = r.result();
                registerCodecs(context);
                registerEvents(context);
                startFuture.complete();
                System.out.println("Spring App Complete..");
            } else {
                startFuture.fail(r.cause());
                System.out.println("Spring App Error..");
            }
        });

        getVertx().eventBus().consumer("test.message", objectMessage -> System.out.println("--------------GOT MSG: " + objectMessage.body()));

        System.out.println("--------------Verticle complete");
    }

    private void registerCodecs(ConfigurableApplicationContext ctx) {
        final EventBus bus = getVertx().eventBus();
        bus.registerDefaultCodec(Address.class, ctx.getBean(AddressCodec.class));
        bus.registerDefaultCodec(Admin.class, ctx.getBean(AdminCodec.class));
        bus.registerDefaultCodec(Area.class, ctx.getBean(AreaCodec.class));
        bus.registerDefaultCodec(AreaCoordinator.class, ctx.getBean(AreaCoordinatorCodec.class));
        bus.registerDefaultCodec(Brand.class, ctx.getBean(BrandCodec.class));
        bus.registerDefaultCodec(Br.class, ctx.getBean(BrCodec.class));
        bus.registerDefaultCodec(BrSupervisor.class, ctx.getBean(BrSupervisorCodec.class));
        bus.registerDefaultCodec(Client.class, ctx.getBean(ClientCodec.class));
        bus.registerDefaultCodec(Consumer.class, ctx.getBean(ConsumerCodec.class));
        bus.registerDefaultCodec(DistributionHouse.class, ctx.getBean(DistributionHouseCodec.class));
        bus.registerDefaultCodec(Employee.class, ctx.getBean(EmployeeCodec.class));
        bus.registerDefaultCodec(HeadOffice.class, ctx.getBean(HeadOfficeCodec.class));
        bus.registerDefaultCodec(Region.class, ctx.getBean(RegionCodec.class));
        bus.registerDefaultCodec(Role.class, ctx.getBean(RoleCodec.class));
        bus.registerDefaultCodec(Town.class, ctx.getBean(TownCodec.class));
        bus.registerDefaultCodec(UserBasic.class, ctx.getBean(UserBasicCodec.class));
        bus.registerDefaultCodec(User.class, ctx.getBean(UserCodec.class));
        bus.registerDefaultCodec(UserIndex.class, ctx.getBean(UserIndexCodec.class));
        bus.registerDefaultCodec(ArrayList.class, ctx.getBean(ArrayListToJsonArrayCodec.class));
    }

    private void registerEvents(ConfigurableApplicationContext ctx) {
        final EventBus bus = getVertx().eventBus();
        bus.consumer(Events.CREATE_NEW_ADMIN, ctx.getBean(AdminService.class)::create);
        bus.consumer(Events.CREATE_NEW_HEAD_OFFICE, ctx.getBean(HeadOfficeService.class)::create);
        bus.consumer(Events.CREATE_NEW_BR, ctx.getBean(BrService.class)::create);
        bus.consumer(Events.CREATE_NEW_BR_SUPERVISOR, ctx.getBean(BrSupervisorService.class)::create);
        bus.consumer(Events.CREATE_NEW_AREA_COORDINATOR, ctx.getBean(AreaCoordinatorService.class)::create);

        bus.consumer(Events.FIND_ALL_TOWNS, ctx.getBean(TownService.class)::findAll);
        bus.consumer(Events.FIND_ALL_DISTRIBUTION_HOUSES, ctx.getBean(DistributionHouseService.class)::findAll);
        bus.consumer(Events.FIND_ALL_BRANDS, ctx.getBean(BrandService.class)::findAll);
    }
}
