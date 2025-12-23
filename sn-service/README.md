# sn-service

Для запуска приложения необходимы java 21 и maven 3.9.х.

Откройте терминал в папке sn-service, и выполните команды:

```
mvn clean package
```
```
docker compose up
```

Затем импортируйте коллекцию из файла sn-service/postman/dialog.postman_collection.json) в Postman.

Далее можно запустить коллекцию целиком, или выполнять запросы по отдельности. 