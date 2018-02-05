FROM nginx:alpine
EXPOSE 18443 18481 18491
COPY nginx.conf /etc/nginx/nginx.conf
COPY nginx.key /etc/nginx/
COPY nginx.crt /etc/nginx/
COPY nifi-cert.pem /etc/nginx/
