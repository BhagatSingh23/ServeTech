# Build stage
FROM node:20-alpine AS build
WORKDIR /app
# Copy package.json and install dependencies
COPY package*.json ./
RUN npm ci
# Copy source code and build the application
COPY . .
# Note: VITE_API_URL should be provided as a build arg or environment variable during the build process
RUN npm run build

# Serve stage
FROM nginx:alpine
# Copy the built Vite assets to Nginx
COPY --from=build /app/dist /usr/share/nginx/html
# Configure Nginx to serve the React SPA and fallback to index.html for routing
RUN echo 'server { \
    listen 80; \
    location / { \
        root /usr/share/nginx/html; \
        index index.html index.htm; \
        try_files $uri $uri/ /index.html; \
    } \
}' > /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
