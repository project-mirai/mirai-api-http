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
    "revision": "8f45145ee78a5bb928939f50daa32e1a"
  },
  {
    "url": "adapter/Adapter.html",
    "revision": "4fcca548d3a38bf114b7b502133c642e"
  },
  {
    "url": "adapter/CustomizedAdapter.html",
    "revision": "f3dd982951cbc9dfcdcd0aabd02060e1"
  },
  {
    "url": "adapter/HttpAdapter.html",
    "revision": "f137453903cfb2c4c79e40587832350a"
  },
  {
    "url": "adapter/ReverseWebsocketAdapter.html",
    "revision": "adb19842b6ac17a7d2d9c3f25e76e29e"
  },
  {
    "url": "adapter/WebhookAdapter.html",
    "revision": "00c8dc456e83ab0294c58ddbfdf9d5fc"
  },
  {
    "url": "adapter/WebsocketAdapter.html",
    "revision": "36c64bf7645b18521c26cc7bcddf1e55"
  },
  {
    "url": "api/API.html",
    "revision": "364fa44516f54eede26045ea7c1efa6d"
  },
  {
    "url": "api/EventType.html",
    "revision": "633ffe232bc3161c7f446bf81add0f93"
  },
  {
    "url": "api/MessageType.html",
    "revision": "65db42e19c50870fd12b74e5eb506c10"
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
    "url": "assets/js/10.bf43f4e1.js",
    "revision": "376f11ecebfd9e93391ce6912d381ac4"
  },
  {
    "url": "assets/js/11.23a7d803.js",
    "revision": "778cda7c8ce0cd4dfc2c6fba833b7acf"
  },
  {
    "url": "assets/js/12.3bfdd598.js",
    "revision": "f96d1e5998c6f5292551b409db097687"
  },
  {
    "url": "assets/js/13.473c6137.js",
    "revision": "4b5ac6bb72252db9aba26e041add8369"
  },
  {
    "url": "assets/js/14.6c5021e5.js",
    "revision": "1407b4ae93205fddc8f18ab4c1e59745"
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
    "url": "assets/js/9.58f680b7.js",
    "revision": "23961bd611fdf5555746472f50a444c7"
  },
  {
    "url": "assets/js/app.d983034c.js",
    "revision": "bf3421733b51118b86dc17d10974721e"
  },
  {
    "url": "index.html",
    "revision": "2a15d2229efa772940678f9a22895808"
  },
  {
    "url": "misc/Migration2.html",
    "revision": "fc3fb37861bd4aa3e9658b6b1193ae5d"
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
