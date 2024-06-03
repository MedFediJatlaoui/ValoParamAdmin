package com.talan.adminmodule.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TreeMapData {
    long numberupdates ;
    List<ParamTableCount> data;
}
