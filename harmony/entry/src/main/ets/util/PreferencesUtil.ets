import { preferences } from '@kit.ArkData';
import { Context } from '@kit.AbilityKit';

class PreferenceUtil {
  // 单例模式
  private static instance: PreferenceUtil = new PreferenceUtil();
  // 首选项名称
  private sp_name: string = 'GreenTreasureStore';
  // 首选项实例
  private pref: preferences.Preferences | null = null;

  // 私有构造函数，确保单例
  private constructor() {}

  /**
   * 获取单例实例
   * @returns PreferenceUtil 实例
   */
  static getInstance(): PreferenceUtil {
    return PreferenceUtil.instance;
  }

  /**
   * 初始化首选项实例
   * @param context 应用上下文
   */
  async init(context: Context) {
    if (this.pref) {
      console.log('首选项已初始化，无需重复初始化');
      return;
    }
    try {
      this.pref = await preferences.getPreferences(context, this.sp_name);
      console.log('首选项初始化成功');
    } catch (err) {
      console.error('首选项初始化失败:', JSON.stringify(err));
    }
  }

  /**
   * 销毁首选项实例
   * @param context 应用上下文
   */
  async deletePreferences(context: Context) {
    if (!this.pref) {
      console.error('首选项未初始化，无法销毁');
      return;
    }
    try {
      await preferences.deletePreferences(context, this.sp_name);
      this.pref = null; // 清空实例
      console.log(`首选项[${this.sp_name}]销毁成功`);
    } catch (err) {
      console.error(`首选项[${this.sp_name}]销毁失败:`, JSON.stringify(err));
    }
  }

  /**
   * 从首选项中获取键对应的值
   * @param key 键名
   * @param defaultValue 默认值
   * @returns 键对应的值，若失败则返回默认值
   */
  async getPreferenceValue(key: string, defaultValue: preferences.ValueType): Promise<preferences.ValueType> {
    if (!this.pref) {
      console.error('首选项未初始化，无法读取数据');
      return defaultValue;
    }
    try {
      const value = await this.pref.get(key, defaultValue);
      console.log(`读取首选项[${key} = ${value}]成功`);
      return value;
    } catch (err) {
      console.error(`读取首选项[${key}]失败:`, JSON.stringify(err));
      return defaultValue;
    }
  }

  /**
   * 向首选项中写入数据
   * @param key 键名
   * @param value 值
   */
  async putPreferenceValue(key: string, value: preferences.ValueType) {
    if (!this.pref) {
      console.error('首选项未初始化，无法写入数据');
      return;
    }
    try {
      await this.pref.put(key, value);
      await this.pref.flush(); // 持久化数据
      console.log(`保存首选项[${key} = ${value}]成功`);
    } catch (err) {
      console.error(`保存首选项[${key} = ${value}]失败:`, JSON.stringify(err));
    }
  }

  /**
   * 从首选项中删除指定键值对
   * @param key 键名
   */
  async deletePreferenceValue(key: string) {
    if (!this.pref) {
      console.error('首选项未初始化，无法删除数据');
      return;
    }
    try {
      await this.pref.delete(key);
      await this.pref.flush(); // 持久化数据
      console.log(`删除首选项[${key}]成功`);
    } catch (err) {
      console.error(`删除首选项[${key}]失败:`, JSON.stringify(err));
    }
  }

  /**
   * 清空首选项中的所有数据
   */
  async clearPreferences() {
    if (!this.pref) {
      console.error('首选项未初始化，无法清空数据');
      return;
    }
    try {
      await this.pref.clear();
      await this.pref.flush(); // 持久化数据
      console.log('清空首选项成功');
    } catch (err) {
      console.error('清空首选项失败:', JSON.stringify(err));
    }
  }
}

// 导出单例实例
export default PreferenceUtil.getInstance() as PreferenceUtil;