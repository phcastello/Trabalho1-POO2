FROM node:20-alpine AS build
WORKDIR /app

# Install dependencies
COPY package*.json ./
RUN npm ci

# Build application
COPY . .
RUN npm run build

FROM nginx:1.27-alpine
# Copy static assets produced by Vite
COPY --from=build /app/dist /usr/share/nginx/html

EXPOSE 80
