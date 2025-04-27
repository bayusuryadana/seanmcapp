import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vitejs.dev/config/
export default defineConfig({
  build: {
    outDir: '../__templates__',
    assetsDir: 'static',
    emptyOutDir: true,
  },
  plugins: [react()],
})
