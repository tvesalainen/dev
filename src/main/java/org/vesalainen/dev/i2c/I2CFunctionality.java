/*
 * Copyright (C) 2016 Timo Vesalainen <timo.vesalainen@iki.fi>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.vesalainen.dev.i2c;

/**
 * Functionality as in i2c.h
 * @author Timo Vesalainen <timo.vesalainen@iki.fi>
 */
public enum I2CFunctionality
{
    /**
     * I2C_FUNC_I2C
     */
    I2C,
    /**
     * I2C_FUNC_10BIT_ADDR
     */
    TenBitAddr,
    /**
     * I2C_FUNC_PROTOCOL_MANGLING
     */
    ProtocolMangling,
    /**
     * I2C_FUNC_SMBUS_PEC
     */
    SMBusPEC,
    /**
     * I2C_FUNC_SMBUS_BLOCK_PROC_CALL
     */
    SMBusBlockProcCall,
    /**
     * I2C_FUNC_SMBUS_QUICK
     */
    SMBusQuick,
    /**
     * I2C_FUNC_SMBUS_READ_BYTE
     */
    SMBusReadByte,
    /**
     * I2C_FUNC_SMBUS_WRITE_BYTE
     */
    SMBusWriteByte,
    /**
     * I2C_FUNC_SMBUS_READ_BYTE_DATA
     */
    SMBusReadByteData,
    /**
     * I2C_FUNC_SMBUS_WRITE_BYTE_DATA
     */
    SMBusWriteByteData,
    /**
     * I2C_FUNC_SMBUS_READ_WORD_DATA
     */
    SMBusReadWordData,
    /**
     * I2C_FUNC_SMBUS_WRITE_WORD_DATA
     */
    SMBusWriteWordData,
    /**
     * I2C_FUNC_SMBUS_PROC_CALL
     */
    SMBusProcCall,
    /**
     * I2C_FUNC_SMBUS_READ_BLOCK_DATA
     */
    SMBusReadBlockData,
    /**
     * I2C_FUNC_SMBUS_WRITE_BLOCK_DATA
     */
    SMBusWriteBlockData,
    /**
     * I2C_FUNC_SMBUS_READ_I2C_BLOCK
     */
    SMBusReadI2CBlock,
    /**
     * I2C_FUNC_SMBUS_WRITE_I2C_BLOCK
     */
    SMBusWriteI2CBlock,

}
