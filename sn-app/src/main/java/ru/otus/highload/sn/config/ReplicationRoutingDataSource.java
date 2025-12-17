package ru.otus.highload.sn.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.concurrent.atomic.AtomicInteger;


@Slf4j
public class ReplicationRoutingDataSource extends AbstractRoutingDataSource {

    private final AtomicInteger counter = new AtomicInteger(0);

    @Override
    protected Object determineCurrentLookupKey() {
        DataSourceType resultType;
        var isReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
        if (isReadOnly) {
            //"балансировка" чтения
            int slaveIndex = counter.getAndIncrement() % 2;
            resultType = slaveIndex == 0 ? DataSourceType.SLAVE_1 : DataSourceType.SLAVE_2;
        } else {
            resultType = DataSourceType.MASTER;
        }
        log.debug("Using {} connection", resultType);
        return resultType;
    }

}
