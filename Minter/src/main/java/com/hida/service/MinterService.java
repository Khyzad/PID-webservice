/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hida.service;

import com.hida.model.BadParameterException;
import com.hida.model.DefaultSetting;
import com.hida.model.Pid;
import com.hida.model.TokenType;
import java.sql.SQLException;
import java.util.Set;

/**
 *
 * @author lruffin
 */
public interface MinterService {            
    public boolean createConnection() throws ClassNotFoundException, SQLException;
    public boolean closeConnection() throws SQLException;
            
    public void createAutoMinter(String prefix, boolean sansVowel,
            TokenType tokenType, int rootLength) throws BadParameterException;    
    public void createCustomMinter(String prefix, boolean sansVowel,
            String charMap) throws BadParameterException;        
    
    
    public Set<Pid> mint(long amount, boolean isRandom) throws SQLException, BadParameterException;
    
    
    public void assignSettings(String prepend, String prefix, TokenType tokenType, int rootLength,
            boolean isAuto, boolean isRandom, boolean sansVowel) throws SQLException;
    public void assignSettings(String prepend, String prefix, String charMap, boolean isAuto,
            boolean isRandom, boolean sansVowel) throws SQLException;
    
    public DefaultSetting getDefaultSetting();
}
