import { defineConfig } from "vite";
import tailwindcss from "@tailwindcss/vite";

export default defineConfig({
  plugins: [tailwindcss()],
   server: {
    proxy: {
      "/api": {
        target: "http://localhost:8080", // <-- change if your backend uses a different port
        changeOrigin: true,
        secure: false
              }
    }
  }
});