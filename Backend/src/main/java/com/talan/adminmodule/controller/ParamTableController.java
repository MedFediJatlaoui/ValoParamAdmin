package com.talan.adminmodule.controller;

import com.talan.adminmodule.dto.*;
import com.talan.adminmodule.entity.ParamAudit;
import com.talan.adminmodule.service.ParamTableService;
import com.talan.adminmodule.service.impl.ParamTableServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping("/api/tables")
@RestController
@CrossOrigin(origins = "*")

public class ParamTableController {
    @Autowired
    private ParamTableService tableService;
    @Autowired
    private HttpServletRequest request;
    @GetMapping("/{limit}/{offset}")
    public ResponseEntity<TablesWithColumns> retrieveAllTablesAndColumns(@PathVariable int limit, @PathVariable int offset) {
        return ResponseEntity.status(HttpStatus.OK).body(tableService.retrieveAllTablesWithFilteredColumns(limit,offset));
    }

    @GetMapping("/{tableName}")
    public ResponseEntity<DataFromTable> getDataFromTable(
            @PathVariable String tableName,
            @ModelAttribute TableDataRequest request
    ) {
        return ResponseEntity.status(HttpStatus.OK).body( tableService.getDataFromTable(
                tableName,
                request
        ));
    }
    @PostMapping("/cancelupdate/{tableName}/{primaryKeyValue}")
    @PreAuthorize("hasRole('ADMIN')or(hasRole('EXPERT') and hasAuthority('CAN_CANCEL'))")
    public ResponseEntity< ResponseDto> cancelupdaterequest(@PathVariable String primaryKeyValue,
                                           @PathVariable String tableName){
        return ResponseEntity.status(HttpStatus.OK).body(tableService.cancelUpdateRequest(primaryKeyValue,tableName));
    }
@GetMapping("/unicity/{tableName}/{primaryKeyValue}")
@PreAuthorize("hasRole('ADMIN') or hasRole('EXPERT')")
public ResponseEntity<Boolean> checkunicity(@PathVariable  String primaryKeyValue ,@PathVariable String tableName){
    return ResponseEntity.status(HttpStatus.OK).body(tableService.checkunicity(primaryKeyValue,tableName));
}
//aaa
   @PutMapping("/update/{tableName}")
   @PreAuthorize("hasRole('ADMIN') or hasRole('EXPERT')")
   public ResponseEntity<ResponseDto> updateInstance(@RequestBody Map<String, String> instanceData,
                                     @PathVariable String tableName) {
       SecurityContextHolderAwareRequestWrapper requestWrapper = new SecurityContextHolderAwareRequestWrapper(request, "ROLE_");
       String username = requestWrapper.getRemoteUser();
       UpdateRequest updateRequest = new UpdateRequest(instanceData,tableName,username);

       return ResponseEntity.status(HttpStatus.OK).body(tableService.addUpdateRequest(updateRequest));

   }
    @PostMapping("/{tableName}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EXPERT')")
    public ResponseEntity<ResponseDto> addInstance(@RequestBody Map<String, String> instanceData,
                                   @PathVariable("tableName") String tableName) {
        return ResponseEntity.status(HttpStatus.OK).body(tableService.addInstance(instanceData, tableName));

    }
    @GetMapping("/dashboard")
    public TreeMapData dataForDashboard()
    {
        return tableService.tablesforDashboard();
    }
    @GetMapping("/{tableName}/fkoptions")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EXPERT')")
    public ResponseEntity<List<ForeignKeyOption>> fkOptions(@PathVariable String tableName)
    {
        return ResponseEntity.status(HttpStatus.OK).body(tableService.foreignKeyoptions(tableName));
    }
    @GetMapping("/{tableName}/references/{primaryKeyValue}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EXPERT')")

    public ResponseEntity<List<DeleteRequest>> checkReferences(@PathVariable String tableName, @PathVariable String primaryKeyValue)
    {
        DeleteRequest del = new DeleteRequest(tableName,primaryKeyValue);
        return ResponseEntity.status(HttpStatus.OK).body(tableService.checkReferenced(del));
    }
    @PostMapping("/{tableName}/cascade/{primaryKeyValue}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EXPERT')")
    public ResponseEntity<ResponseDto> deleteCascade(@PathVariable String tableName, @PathVariable String primaryKeyValue){
       try{
           DeleteRequest del = new DeleteRequest(tableName,primaryKeyValue);
           return  ResponseEntity.status(HttpStatus.OK).body(tableService.deletecascade(del));

       }catch (Exception e) {
           ResponseDto result = new ResponseDto();
           result.setError("Cascade Delete Failed");
return ResponseEntity.status(HttpStatus.FORBIDDEN).body(result);

       }
    }
   @GetMapping("/{tableName}/delete/{primaryKeyValue}")
   @PreAuthorize("hasRole('ADMIN') or hasRole('EXPERT')")
   public ResponseEntity<ResponseDto> deleteRecord(@PathVariable String tableName, @PathVariable String primaryKeyValue) {
    SecurityContextHolderAwareRequestWrapper requestWrapper = new SecurityContextHolderAwareRequestWrapper(request, "ROLE_");
    String username = requestWrapper.getRemoteUser();
    DeleteRequest deleteRequest = new DeleteRequest(tableName, primaryKeyValue,username);

    return ResponseEntity.status(HttpStatus.OK).body(tableService.addDeleteRequest(deleteRequest));

}
    @PostMapping("/{tableName}/canceldeletion/{primaryKeyValue}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('EXPERT') and hasAuthority('CAN_CANCEL'))")     public ResponseEntity<ResponseDto> canceldeleterequest(@PathVariable String tableName, @PathVariable String primaryKeyValue){
        return ResponseEntity.status(HttpStatus.OK).body(tableService.cancelDeleteRequest(tableName,primaryKeyValue));
    }
    @GetMapping("/{tableName}/history")
       public ResponseEntity<List<ParamAudit>> paramHistory(@PathVariable String tableName){
        return ResponseEntity.status(HttpStatus.OK).body(tableService.paramHistory(tableName));
    }
    @GetMapping("/all/history")
    public ResponseEntity<List<ParamAudit>> allparamHistory(){
        return ResponseEntity.status(HttpStatus.OK).body(tableService.allparamHistory());
    }
    @GetMapping("/deletereq/{tableName}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EXPERT')")
    public ResponseEntity<List<Map<String, Object>>> alldeleterequests(@PathVariable String tableName){
        return ResponseEntity.status(HttpStatus.OK).body(tableService.getRowsForDeleteRequests(tableName));
    }

    @GetMapping("/updatereq/{tableName}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EXPERT')")
    public ResponseEntity<List<UpdateRequest>> allupdaterequests(@PathVariable String tableName){
        return ResponseEntity.status(HttpStatus.OK).body(tableService.getUpdateRequestByTable(tableName));
    }

}
