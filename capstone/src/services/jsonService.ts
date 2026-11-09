import { jsonRepository } from '../repositories/jsonRepository';

class JsonService {
  async create(data: any) {
    if (!data || Object.keys(data).length === 0) {
      throw new Error('Data is required');
    }
    const id = Date.now().toString();
    const newData = { id, ...data };
    await jsonRepository.create(id, newData);
    return newData;
  }

  async get(id: string) {
    const data = await jsonRepository.get(id);
    if (!data) {
      throw new Error('Data not found');
    }
    return data;
  }

  async update(id: string, data: any) {
    if (!data || Object.keys(data).length === 0) {
      throw new Error('Data is required');
    }
    const existingData = await jsonRepository.get(id);
    if (!existingData) {
      throw new Error('Data not found');
    }
    const updatedData = { ...existingData, ...data };
    await jsonRepository.update(id, updatedData);
    return updatedData;
  }

  async patch(id: string, data: any) {
    if (!data || Object.keys(data).length === 0) {
      throw new Error('Data is required');
    }
    const existingData = await jsonRepository.get(id);
    if (!existingData) {
      throw new Error('Data not found');
    }
    const updatedData = { ...existingData, ...data };
    await jsonRepository.update(id, updatedData);
    return updatedData;
  }

  async delete(id: string) {
    const existingData = await jsonRepository.get(id);
    if (!existingData) {
      throw new Error('Data not found');
    }
    await jsonRepository.delete(id);
  }
}

export const jsonService = new JsonService();
