<template>
  <div class="admin-layout">
    <aside class="admin-sidebar">
      <h3>管理后台</h3>
      <router-link to="/admin">数据概览</router-link>
      <router-link to="/admin/users">用户管理</router-link>
      <router-link to="/admin/content">内容管理</router-link>
      <router-link to="/admin/ai">AI 管控</router-link>
      <router-link to="/">返回前台</router-link>
    </aside>
    <main class="admin-main">
      <router-view />
    </main>
  </div>
</template>

<style scoped>
.admin-layout { display: flex; min-height: calc(100vh - 56px); }
.admin-sidebar { width: 200px; background: var(--bg-card); border-right: 1px solid var(--border); padding: 20px; display: flex; flex-direction: column; gap: 12px; }
.admin-sidebar h3 { margin-bottom: 16px; }
.admin-sidebar a { color: var(--text); font-size: 14px; padding: 8px; border-radius: var(--radius); }
.admin-sidebar a:hover, .admin-sidebar a.router-link-active { background: var(--primary); color: #fff; text-decoration: none; }
.admin-main { flex: 1; padding: 20px; }
</style>
