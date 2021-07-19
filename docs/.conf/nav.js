module.exports = {
    text: 'mirai-api-http',
    items: [
        { text: "首页", link: "/" },
        { text: "API", link: "/api/API.html" },
        { text: "事件类型", link: "/api/EventType.html" },
        { text: "消息类型", link: "/api/MessageType.html" },
        {
            text: "接口适配器",
            items: [
                { text: "http轮询", link: "/adapter/HttpAdapter.html" },
                { text: "websocket", link: "/adapter/WebsocketAdapter.html" },
                { text: "反向websocket", link: "/adapter/ReverseWebsocketAdapter.html" },
                { text: "webhook上报", link: "/adapter/WebhookAdapter.html" },
            ],
        },
    ],
};
