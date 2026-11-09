// src/repositories/orderRepository.ts
import fs from 'fs/promises';
import path from 'path';
import { Order } from '../types';

const dataDir = path.join(__dirname, '../data');

class OrderRepository {
  private async ensureDataDir() {
    try {
      await fs.access(dataDir);
    } catch (error) {
      await fs.mkdir(dataDir, { recursive: true });
    }
  }

  async create(order: Order): Promise<void> {
    await this.ensureDataDir();
    const filePath = path.join(dataDir, `${order.id}.json`);
    await fs.writeFile(filePath, JSON.stringify(order, null, 2));
  }

  async findById(id: string): Promise<Order | null> {
    const filePath = path.join(dataDir, `${id}.json`);
    try {
      const data = await fs.readFile(filePath, 'utf-8');
      return JSON.parse(data);
    } catch (error: any) {
      if (error.code === 'ENOENT') {
        return null; // File not found
      }
      throw new Error(`Error reading order file: ${error.message}`);
    }
  }

  async findAll(): Promise<Order[]> {
    await this.ensureDataDir();
    const files = await fs.readdir(dataDir);
    const orderFiles = files.filter(file => file.endsWith('.json') && !file.includes('items.json') && !file.includes('users.json')); // Exclude items.json and users.json

    const orders: Order[] = [];
    for (const file of orderFiles) {
      try {
        const data = await fs.readFile(path.join(dataDir, file), 'utf-8');
        const order = JSON.parse(data);
        // Basic validation to ensure it's an order object
        if (order.id && order.customerId && order.items && order.total !== undefined) {
          orders.push(order);
        }
      } catch (error) {
        console.error(`Error reading or parsing file ${file}:`, error);
      }
    }
    return orders;
  }

  async update(id: string, updatedOrder: Order): Promise<void> {
    await this.ensureDataDir();
    const filePath = path.join(dataDir, `${id}.json`);
    await fs.writeFile(filePath, JSON.stringify(updatedOrder, null, 2));
  }

  async delete(id: string): Promise<boolean> {
    const filePath = path.join(dataDir, `${id}.json`);
    try {
      await fs.unlink(filePath);
      return true;
    } catch (error: any) {
      if (error.code === 'ENOENT') {
        return false; // File not found
      }
      throw new Error(`Error deleting order file: ${error.message}`);
    }
  }
}

export const orderRepository = new OrderRepository();
