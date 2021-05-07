/**
 * Welcome to your Workbox-powered service worker!
 *
 * You'll need to register this file in your web app and you should
 * disable HTTP caching for this file too.
 * See https://goo.gl/nhQhGp
 *
 * The rest of the code is auto-generated. Please don't update this file
 * directly; instead, make changes to your Workbox build configuration
 * and re-run your build process.
 * See https://goo.gl/2aRDsh
 */

importScripts("https://storage.googleapis.com/workbox-cdn/releases/4.3.1/workbox-sw.js");

self.addEventListener('message', (event) => {
  if (event.data && event.data.type === 'SKIP_WAITING') {
    self.skipWaiting();
  }
});

/**
 * The workboxSW.precacheAndRoute() method efficiently caches and responds to
 * requests for URLs in the manifest.
 * See https://goo.gl/S9QRab
 */
self.__precacheManifest = [
  {
    "url": "404.html",
    "revision": "7502c8ce526c6bd7c33c84ad447c77f3"
  },
  {
    "url": "API.html",
    "revision": "5707bda6fd80b3030671063d831279b7"
  },
  {
    "url": "assets/css/0.styles.d298e7d4.css",
    "revision": "a9f716797a39868a2c924dbd8700c426"
  },
  {
    "url": "assets/img/search.83621669.svg",
    "revision": "83621669651b9a3d4bf64d1a670ad856"
  },
  {
    "url": "assets/img/search.867d45d8.svg",
    "revision": "867d45d8f9c0da0e3e733dd5e7a8d263"
  },
  {
    "url": "assets/js/10.536f15cb.js",
    "revision": "0d23d2b6f0afc0d95f604797876fc063"
  },
  {
    "url": "assets/js/11.97c4a0b3.js",
    "revision": "7a9024478e95a07b83971420d511dae3"
  },
  {
    "url": "assets/js/12.019aa674.js",
    "revision": "5baf339f84926486eacec5b903bdd412"
  },
  {
    "url": "assets/js/2.10b98dd9.js",
    "revision": "565320893b0787730e837c9f6965618b"
  },
  {
    "url": "assets/js/3.394b0e42.js",
    "revision": "da6df5c47b3054502f717f99064f2b99"
  },
  {
    "url": "assets/js/4.581ad1eb.js",
    "revision": "21cf220186485b613b13a85a0446d080"
  },
  {
    "url": "assets/js/5.03f40231.js",
    "revision": "f91b3a20f085579f3388dc1723807b27"
  },
  {
    "url": "assets/js/6.7c4c6923.js",
    "revision": "3a071d2ec003d0883b944046948e566b"
  },
  {
    "url": "assets/js/7.0cf748a6.js",
    "revision": "c69059b15b16593f8440c743ef7cabd8"
  },
  {
    "url": "assets/js/8.ce38cfba.js",
    "revision": "cfe783e7155ada73082dc8712e1fad4d"
  },
  {
    "url": "assets/js/9.0813d974.js",
    "revision": "eb9f248edee9a1c15a2bec578ff3d416"
  },
  {
    "url": "assets/js/app.c51fdb0e.js",
    "revision": "be960e1e7a6b70015841fdecb8c438d3"
  },
  {
    "url": "EventType.html",
    "revision": "593405bee7bb6d72598e5887b3051d73"
  },
  {
    "url": "Heartbeat.html",
    "revision": "0b13c824690d885af694c63e8ab15e32"
  },
  {
    "url": "index.html",
    "revision": "b1270a84893d01ca55b3aa1fc66333fc"
  },
  {
    "url": "MessageType.html",
    "revision": "bd083af245da541972568c3d8831e8b9"
  },
  {
    "url": "Report.html",
    "revision": "6da9a35e94ed66ba7eab0246b392a541"
  }
].concat(self.__precacheManifest || []);
workbox.precaching.precacheAndRoute(self.__precacheManifest, {});
addEventListener('message', event => {
  const replyPort = event.ports[0]
  const message = event.data
  if (replyPort && message && message.type === 'skip-waiting') {
    event.waitUntil(
      self.skipWaiting().then(
        () => replyPort.postMessage({ error: null }),
        error => replyPort.postMessage({ error })
      )
    )
  }
})
