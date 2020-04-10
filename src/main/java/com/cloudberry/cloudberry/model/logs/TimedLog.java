package com.cloudberry.cloudberry.model.logs;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public abstract class TimedLog extends Log {
    protected Date eventTime;
}
