# Stage 1: Build the frontend using Node
FROM node:18-alpine AS build

WORKDIR /app

# Copy package files and install dependencies
COPY package*.json ./
RUN npm install

# Copy the rest of your frontend source code
COPY . .

# Run the build command to generate 'dist' folder with assets
RUN npm run build

# Stage 2: Serve the built files with Nginx
FROM nginx:stable-alpine

# Remove default static content
RUN rm -rf /usr/share/nginx/html/*

# Copy built files from Stage 1
COPY --from=build /app/dist /usr/share/nginx/html/

# Expose port 80
EXPOSE 80

# Start Nginx in foreground
CMD ["nginx", "-g", "daemon off;"]
