FROM nginx:alpine
EXPOSE 7444 7481
COPY nginx.conf /etc/nginx/nginx.conf
COPY nginx.key /etc/nginx/
COPY nginx.crt /etc/nginx/
COPY nifi-cert.pem /etc/nginx/
