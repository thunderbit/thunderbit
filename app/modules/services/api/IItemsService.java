package modules.services.api;

import models.Item;
import play.libs.F;

import java.util.List;

public interface IItemsService {
    /**
     * Creates an item
     *
     * @param   name
     *          The item's name
     * @param   storageKey
     *          The item's storage key
     * @param   tags
     *          The item's tags
     * @return  A promise of creating the item
     */
    F.Promise<Item> create (String name, String storageKey, List<String> tags);

    /**
     * Retrieves an item
     *
     * @param   id
     *          The item's id
     * @return  A promise of creating the item
     */
    F.Promise<Item> read (String id);

    /**
     * Deletes an item
     *
     * @param   id
     *          The item's id
     * @return  A promise of retrieving the item
     */
    F.Promise<Item> delete (String id);

    /**
     * Retrieves all items
     *
     * @return  A promise of retrieving all items
     */
    F.Promise<List<Item>> findAll ();
}
