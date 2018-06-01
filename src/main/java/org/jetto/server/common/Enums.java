package org.jetto.server.common;

/**
 *
 * @author gorkemsari - jetto.org
 */
public class Enums {
    public enum Type {
        SERVER(1000),
        FORWARD(1001);

        public final int Value;

        private Type(int value)
        {
            Value = value;
        }
    }

    public enum SubType {
        REGISTER(1000),
        HANDSHAKE(1001);

        public final int Value;

        private SubType(int value)
        {
            Value = value;
        }
    }
}