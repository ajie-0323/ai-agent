import axios from "axios";

const request = axios.create({
  baseURL: "/api",
  timeout: 30000,
});

export default request;