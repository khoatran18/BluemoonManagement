FROM node:20-alpine AS build

WORKDIR /app

COPY package*.json ./
RUN npm ci 

COPY . .

ARG API_BASE_URL

ENV API_BASE_URL=$API_BASE_URL

RUN npm run build

FROM nginx:stable-alpine

RUN rm /etc/nginx/conf.d/default.conf

COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]