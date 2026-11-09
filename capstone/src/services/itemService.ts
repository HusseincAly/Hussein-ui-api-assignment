// src/services/itemService.ts
import { itemRepository } from '../repositories/itemRepository';
import { Item } from '../types';
import ApiError from '../utils/ApiError';

class ItemService {
  async findAll(): Promise<Item[]> {
    return itemRepository.findAll();
  }

  async findById(id: string): Promise<Item> {
    const item = await itemRepository.findById(id);
    if (!item) {
      throw new ApiError(404, `Item with ID ${id} not found.`);
    }
    return item;
  }

  async create(itemData: Omit<Item, 'id'>): Promise<Item> {
    // These validations are also covered by Joi schema, but good to have service-level checks too
    if (!itemData.name || itemData.price === undefined || itemData.stock === undefined) {
      throw new ApiError(400, 'Item name, price, and stock are required.');
    }
    if (itemData.price <= 0) {
      throw new ApiError(400, 'Item price must be positive.');
    }
    if (itemData.stock < 0) {
      throw new ApiError(400, 'Item stock cannot be negative.');
    }
    return itemRepository.create(itemData);
  }

  async update(id: string, itemData: Partial<Item>): Promise<Item> {
    const existingItem = await itemRepository.findById(id);
    if (!existingItem) {
      throw new ApiError(404, `Item with ID ${id} not found.`);
    }

    if (itemData.price !== undefined && itemData.price <= 0) {
      throw new ApiError(400, 'Item price must be positive.');
    }
    if (itemData.stock !== undefined && itemData.stock < 0) {
      throw new ApiError(400, 'Item stock cannot be negative.');
    }

    const updatedItem = { ...existingItem, ...itemData, id };
    const result = await itemRepository.update(id, updatedItem);
    if (!result) {
        // This case should ideally not happen if findById passed
        throw new ApiError(500, `Failed to update item with ID ${id} for an unknown reason.`);
    }
    return result;
  }

  async delete(id: string): Promise<void> {
    const deleted = await itemRepository.delete(id);
    if (!deleted) {
      throw new ApiError(404, `Item with ID ${id} not found.`);
    }
  }
}

export const itemService = new ItemService();
