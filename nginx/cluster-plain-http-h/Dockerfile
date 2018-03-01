FROM nginx:alpine
RUN apk --no-cache add dnsmasq
EXPOSE 53 53/udp 17180
COPY nginx.conf /etc/nginx/nginx.conf
