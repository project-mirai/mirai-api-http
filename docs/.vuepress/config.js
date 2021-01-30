module.exports = {
  title: 'mirai-api-http',
  description: 'Mirai HTTP API (console) plugin',
  markdown: {
    lineNumbers: true
  },
  theme: "antdocs",
  themeConfig: {
    sidebar: 'auto',
    sidebarDepth: 4,
    displayAllHeaders: true
  },
  plugins: [
    "@vuepress/plugin-medium-zoom",
    [
      "@vuepress/pwa",
      {
        serviceWorker: true,
        updatePopup: {
          message: "发现新内容",
          buttonText: "刷新"
        }
      }
    ],
  ]
}
