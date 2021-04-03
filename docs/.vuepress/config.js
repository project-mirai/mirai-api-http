module.exports = {
  title: "mirai-api-http",
  description: "Mirai HTTP API (console) plugin",
  base: "mirai-api-http",
  markdown: {
    lineNumbers: true,
  },
  theme: "antdocs",
  themeConfig: {
    backToTop:true,
    sidebar: "auto",
    sidebarDepth: 2,
    displayAllHeaders: true,
    repo: "project-mirai/mirai-api-http",
    logo: "https://raw.githubusercontent.com/mamoe/mirai/dev/docs/mirai.png",
    docsDir: "docs",
    editLinks: true,
    smoothScroll: true,
    editLinkText: "在 GitHub 上编辑此页",
    lastUpdated: "上次更新",
    nav: [
      { text: "首页", link: "/" },
      { text: "API", link: "/API/" },
      { text: "事件类型", link: "/EventType/" },
      { text: "消息类型", link: "/MessageType/" },
      {
        text: "其他",
        items: [
          { text: "心跳服务", link: "/Heartbeat/" },
          { text: "事件上报", link: "/Report/" },
        ],
      },
    ],
  },
  plugins: [
    "@vuepress/plugin-medium-zoom",
    "@vuepress/nprogress",
    [
      "@vuepress/pwa",
      {
        serviceWorker: true,
        updatePopup: {
          message: "发现新内容",
          buttonText: "刷新",
        },
      },
    ],
  ],
};
