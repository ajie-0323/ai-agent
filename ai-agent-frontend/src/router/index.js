import { createRouter, createWebHistory } from "vue-router";
import Home from "../views/Home.vue";

const routes = [
  {
    path: "/",
    name: "Home",
    component: Home,
  },
  {
    path: "/love-chat",
    name: "LoveChat",
    component: () => import("../views/LoveChat.vue"),
  },
  {
    path: "/manus-chat",
    name: "ManusChat",
    component: () => import("../views/ManusChat.vue"),
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;