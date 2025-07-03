/**
 * 自动生成路由配置
 * 使用 Vite 的 import.meta.glob 功能
 */
export function generateRoutes() {
  // 使用 Vite 的 import.meta.glob 来获取所有 Vue 文件
  const modules = import.meta.glob('../views/**/*.vue')
  
  const routes = []
  
  // 预定义的路由配置
  const routeConfig = {
    dashboard: {
      meta: { title: '仪表板', icon: 'dashboard', requiresAuth: true }
    },
    fund: {
      meta: { title: '基金研究', icon: 'fund' },
      children: {
        list: { title: '基金列表', permissions: ['fund:view'] },
        search: { title: '基金筛选', permissions: ['fund:view'] },
        detail: { title: '基金详情', permissions: ['fund:view'], isDynamic: true }
      }
    },
    factor: {
      meta: { title: '因子管理', icon: 'factor' },
      children: {
        list: { title: '因子列表', permissions: ['factor:view'] }
      }
    },
    strategy: {
      meta: { title: '策略管理', icon: 'strategy' },
      children: {
        list: { title: '策略列表', permissions: ['strategy:view'] }
      }
    },
    product: {
      meta: { title: '组合产品', icon: 'product' },
      children: {
        list: { title: '产品列表', permissions: ['product:view'] }
      }
    },
    trade: {
      meta: { title: '交易管理', icon: 'trade' },
      children: {
        rebalance: { title: '组合调仓', permissions: ['trade:rebalance'] }
      }
    }
  }
  
  // 根据实际存在的文件生成路由
  Object.keys(modules).forEach(filePath => {
    const route = createRouteFromPath(filePath, modules[filePath], routeConfig)
    if (route) {
      routes.push(route)
    }
  })
  
  return organizeRoutes(routes)
}

/**
 * 根据文件路径创建路由
 */
function createRouteFromPath(filePath, component, routeConfig) {
  // 解析文件路径: ../views/dashboard/index.vue -> dashboard/index
  const pathMatch = filePath.match(/\.\.\/views\/(.+)\.vue$/)
  if (!pathMatch) return null
  
  const routePath = pathMatch[1]
  const pathParts = routePath.split('/')
  
  // 跳过 auth 目录（登录页面单独处理）
  if (pathParts[0] === 'auth') return null
  
  const moduleName = pathParts[0]
  const fileName = pathParts[pathParts.length - 1]
  
  // 处理 dashboard
  if (moduleName === 'dashboard' && fileName === 'index') {
    return {
      path: 'dashboard',
      name: 'Dashboard',
      component,
      meta: routeConfig.dashboard?.meta || { title: '仪表板', requiresAuth: true }
    }
  }
  
  // 处理其他模块
  const moduleConfig = routeConfig[moduleName]
  if (!moduleConfig) return null
  
  if (fileName === 'index') {
    // 模块首页，跳过
    return null
  }
  
  const childConfig = moduleConfig.children?.[fileName]
  if (!childConfig) return null
  
  const routeItem = {
    path: childConfig.isDynamic ? `:id/${fileName}` : fileName,
    name: `${capitalize(moduleName)}${capitalize(fileName)}`,
    component,
    meta: {
      title: childConfig.title,
      permissions: childConfig.permissions || [],
      requiresAuth: true
    }
  }
  
  // 标记所属模块
  routeItem._module = moduleName
  
  return routeItem
}

/**
 * 组织路由结构
 */
function organizeRoutes(routes) {
  const organizedRoutes = []
  const moduleRoutes = {}
  
  // 按模块分组
  routes.forEach(route => {
    if (route._module) {
      const moduleName = route._module
      if (!moduleRoutes[moduleName]) {
        moduleRoutes[moduleName] = {
          path: moduleName,
          name: capitalize(moduleName),
          meta: getModuleMeta(moduleName),
          children: []
        }
      }
      delete route._module
      moduleRoutes[moduleName].children.push(route)
    } else {
      organizedRoutes.push(route)
    }
  })
  
  // 添加模块路由
  Object.values(moduleRoutes).forEach(moduleRoute => {
    // 添加默认重定向到第一个子路由
    if (moduleRoute.children.length > 0) {
      moduleRoute.redirect = `/${moduleRoute.path}/${moduleRoute.children[0].path}`
    }
    organizedRoutes.push(moduleRoute)
  })
  
  return organizedRoutes
}

/**
 * 获取模块元信息
 */
function getModuleMeta(moduleName) {
  const metaMap = {
    fund: { title: '基金研究', icon: 'fund', requiresAuth: true },
    factor: { title: '因子管理', icon: 'factor', requiresAuth: true },
    strategy: { title: '策略管理', icon: 'strategy', requiresAuth: true },
    product: { title: '组合产品', icon: 'product', requiresAuth: true },
    trade: { title: '交易管理', icon: 'trade', requiresAuth: true }
  }
  
  return metaMap[moduleName] || { title: moduleName, requiresAuth: true }
}

/**
 * 首字母大写
 */
function capitalize(str) {
  return str.charAt(0).toUpperCase() + str.slice(1)
} 