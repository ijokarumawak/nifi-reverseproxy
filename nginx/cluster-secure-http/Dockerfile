FROM nginx:alpine
RUN apk --no-cache add dnsmasq
EXPOSE 17443
COPY nginx.conf /etc/nginx/nginx.conf
COPY nginx.key /etc/nginx/
COPY nginx.crt /etc/nginx/
COPY nifi-cert.pem /etc/nginx/
