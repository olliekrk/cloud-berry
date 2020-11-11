FROM node:15.1.0-alpine AS builder
COPY . ./cloudberry-ng
WORKDIR /cloudberry-ng
RUN npm i
RUN $(npm bin)/ng build --prod

FROM nginx:1.19.4-alpine
COPY --from=builder /cloudberry-ng/dist/cloudberry-ng/ /usr/share/nginx/html
