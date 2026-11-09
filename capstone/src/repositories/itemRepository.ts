// src/repositories/itemRepository.ts
import fs from 'fs/promises';
import path from 'path';
import { Item } from '../types';

const dataDir = path.join(__dirname, '../data');
const itemsFilePath = path.join(dataDir, 'items.json');

class ItemRepository {
  private async ensureDataDir() {
    try {
      await fs.access(dataDir);
    } catch (error) {
      await fs.mkdir(dataDir, { recursive: true });
    }
  }

  private async readItemsFile(): Promise<Item[]> {
    await this.ensureDataDir();
    try {
      const data = await fs.readFile(itemsFilePath, 'utf-8');
      return JSON.parse(data);
    } catch (error: any) {
      if (error.code === 'ENOENT') {
        // If file does not exist, return an empty array
        return [];
      }
      throw new Error(`Error reading items file: ${error.message}`);
    }
  }

  private async writeItemsFile(items: Item[]): Promise<void> {
    await this.ensureDataDir();
    await fs.writeFile(itemsFilePath, JSON.stringify(items, null, 2));
  }

  async findAll(): Promise<Item[]> {
    return this.readItemsFile();
  }

  async findById(id: string): Promise<Item | null> {
    const items = await this.readItemsFile();
    return items.find(item => item.id === id) || null;
  }

  async create(item: Omit<Item, 'id'>): Promise<Item> {
    const items = await this.readItemsFile();
    const newItem: Item = { id: Date.now().toString(), ...item };
    items.push(newItem);
    await this.writeItemsFile(items);
    return newItem;
  }

  async update(id: string, updatedItem: Item): Promise<Item | null> {
    const items = await this.readItemsFile();
    const index = items.findIndex(item => item.id === id);
    if (index === -1) {
      return null;
    }
    items[index] = { ...items[index], ...updatedItem, id }; // Ensure ID remains the same
    await this.writeItemsFile(items);
    return items[index];
  }

  async delete(id: string): Promise<boolean> {
    const items = await this.readItemsFile();
    const initialLength = items.length;
    const filteredItems = items.filter(item => item.id !== id);
    if (filteredItems.length === initialLength) {
      return false; // Item not found
    }
    await this.writeItemsFile(filteredItems);
    return true;
  }
}

export const itemRepository = new ItemRepository();
