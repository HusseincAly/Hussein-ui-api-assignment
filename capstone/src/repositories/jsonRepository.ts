import fs from 'fs/promises';
import path from 'path';

const dataDir = path.join(__dirname, '../data');

class JsonRepository {
  private async ensureDataDir() {
    try {
      await fs.access(dataDir);
    } catch (error) {
      await fs.mkdir(dataDir, { recursive: true });
    }
  }

  async create(id: string, data: any) {
    await this.ensureDataDir();
    const filePath = path.join(dataDir, `${id}.json`);
    await fs.writeFile(filePath, JSON.stringify(data, null, 2));
  }

  async get(id: string) {
    const filePath = path.join(dataDir, `${id}.json`);
    try {
      const data = await fs.readFile(filePath, 'utf-8');
      return JSON.parse(data);
    } catch (error) {
      return null;
    }
  }

  async update(id: string, data: any) {
    await this.ensureDataDir();
    const filePath = path.join(dataDir, `${id}.json`);
    await fs.writeFile(filePath, JSON.stringify(data, null, 2));
  }

  async delete(id: string) {
    const filePath = path.join(dataDir, `${id}.json`);
    try {
      await fs.unlink(filePath);
    } catch (error) {
        if (error instanceof Error) {
            throw new Error('Error deleting file');
        }
    }
  }
}

export const jsonRepository = new JsonRepository();
