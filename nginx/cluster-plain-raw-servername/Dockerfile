FROM nginx:alpine
RUN apk --no-cache add dnsmasq
EXPOSE 17190 17191
COPY nginx.conf /etc/nginx/nginx.conf
