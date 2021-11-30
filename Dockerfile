FROM quay.io/ukhomeofficedigital/alpine:v3.13

ENV USER user-hocs-bulk-case-action
ENV USER_ID 1000
ENV GROUP group-hocs-bulk-case-action
ENV NAME hocs-bulk-case-action
ENV JAR_PATH build/libs

ENV workflow-address http://host.docker.internal:8091
ENV x-auth-groups /COMP_CASEADMIN_bdhvyvg
ENV x-auth-userId 968672ca-9fc1-491c-b2b9-d13c58b12d7a
ENV gap-between-updates 5000
ENV file-path ./test.csv

USER root

RUN apk add openjdk11-jre

WORKDIR /app

RUN addgroup -S ${GROUP} && \
    adduser -S -u ${USER_ID} ${USER} -G ${GROUP} -h /app && \
    mkdir -p /app && \
    chown -R ${USER}:${GROUP} /app

COPY ${JAR_PATH}/${NAME}*.jar /app

COPY test.csv /app

ADD scripts /app/scripts

RUN chmod a+x /app/scripts/*

EXPOSE 8080

USER ${USER_ID}

CMD ["sh", "/app/scripts/run.sh"]