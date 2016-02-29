package modules.services;

import com.google.inject.AbstractModule;
import modules.services.api.IItemsService;
import modules.services.imp.ItemsService;

public class ServicesModule extends AbstractModule {
    @Override
    protected void configure() {
        bind (IItemsService.class).to(ItemsService.class);
    }
}
