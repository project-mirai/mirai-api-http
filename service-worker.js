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
    "revision": "d70e1d0dcafa4d69184e24d422b24457"
  },
  {
    "url": "adapter/Adapter.html",
    "revision": "07f73a1e0742b57ee067c6176a39cca1"
  },
  {
    "url": "adapter/CustomizedAdapter.html",
    "revision": "40868c6caa6168ef9878bf4996f04c49"
  },
  {
    "url": "adapter/HttpAdapter.html",
    "revision": "2436a28052452c1b07ea45a3b9eb644b"
  },
  {
    "url": "adapter/ReverseWebsocketAdapter.html",
    "revision": "d272ab8e2d6d0c3cf0543c4c374c1beb"
  },
  {
    "url": "adapter/WebhookAdapter.html",
    "revision": "d15697fd3a350e11ea2c0d5e8e1143e4"
  },
  {
    "url": "adapter/WebsocketAdapter.html",
    "revision": "80dffe2a40d22eb2d62b09d146a08856"
  },
  {
    "url": "api/API.html",
    "revision": "f1cf9beaa6342907ea50af1391c720d6"
  },
  {
    "url": "api/EventType.html",
    "revision": "bb24fda60f5a7467736fd5aa19b279c4"
  },
  {
    "url": "api/MessageType.html",
    "revision": "1120cb2e42a1e8f8c9dd331f096e12c4"
  },
  {
    "url": "assets/css/0.styles.96f1c4e5.css",
    "revision": "b7f144b0330972e00049a38294438812"
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
    "url": "assets/js/10.9a0a1cac.js",
    "revision": "c27d94a520a3f4283633c06ab24b6edf"
  },
  {
    "url": "assets/js/11.9b3e1e93.js",
    "revision": "62b09e8b08a224ccfdbf34499d864917"
  },
  {
    "url": "assets/js/12.9508b5fe.js",
    "revision": "9e25a6d5e97a71d6494b1b98727f36ce"
  },
  {
    "url": "assets/js/13.e74e7f3c.js",
    "revision": "1224772f49c6df868dd5494d9e4e3ee2"
  },
  {
    "url": "assets/js/14.57ebb766.js",
    "revision": "d8d82e6f73847f92d5a272b1d0b27230"
  },
  {
    "url": "assets/js/15.bbe08107.js",
    "revision": "1c41164ea3737dcc4920b0ca99674fe5"
  },
  {
    "url": "assets/js/16.54453210.js",
    "revision": "9ecc7503526e51ddc76460db8eba8682"
  },
  {
    "url": "assets/js/17.008167a6.js",
    "revision": "739a9cfed487959084ce282e16073e39"
  },
  {
    "url": "assets/js/2.6e08f815.js",
    "revision": "b4db765ae3ddd673f9579a85bb49d9ec"
  },
  {
    "url": "assets/js/3.d7b7466a.js",
    "revision": "b7e73b62e9ac2c0537385f623262f545"
  },
  {
    "url": "assets/js/4.0b584fb5.js",
    "revision": "6ee7b3a72cd38f7facc0fcaf96578ca3"
  },
  {
    "url": "assets/js/5.b3486ee3.js",
    "revision": "ed70bb45d7e695a1e441ff7808657771"
  },
  {
    "url": "assets/js/6.725f79ac.js",
    "revision": "8dc4ff80c5254b4a636421ad1eed5e6a"
  },
  {
    "url": "assets/js/7.1af437d3.js",
    "revision": "9c622675844004a23384f3a8714bad80"
  },
  {
    "url": "assets/js/8.7f3d32fb.js",
    "revision": "5097247512a29568a6187ab48861ca6f"
  },
  {
    "url": "assets/js/9.683be53e.js",
    "revision": "66cdbeb01f349e21ace1225b0146612a"
  },
  {
    "url": "assets/js/app.e68d7eb2.js",
    "revision": "14969c1504ad38edf8c120c0638e01dd"
  },
  {
    "url": "index.html",
    "revision": "52cbba77f8268e0745a5b5d649f66b0d"
  },
  {
    "url": "misc/Migration2.html",
    "revision": "04bebc6dc2ab5c2444461121f05f2c7a"
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
