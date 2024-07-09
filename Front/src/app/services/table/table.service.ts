import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {TablesWithColumns} from "../../model/tables-with-columns";
import {TableInfo} from "../../model/table-info";
import {ParamAudit} from "../../model/param-audit";
@Injectable({
  providedIn: 'root'
})
export class TableService {
  baseUrl = 'http://localhost:8090/api/tables';
  constructor(private http : HttpClient) {
  }
dataDeleteInstance:any;
  deleteRecord(tableName: string, primaryKeyValue: string): Observable<any> {
    return this.http.get<string>(`${this.baseUrl}/${tableName}/delete/${primaryKeyValue}`, {},);
  }
  cancelDeletion(tableName: string, primaryKeyValue: string): Observable<any> {
    return this.http.post<string>(`${this.baseUrl}/${tableName}/canceldeletion/${primaryKeyValue}`, {},);
  }
  deleteCascade(tableName:string,primaryKeyValue:string):Observable<any>{
    return this.http.post<string>(`${this.baseUrl}/${tableName}/cascade/${primaryKeyValue}`,{},)
  }
  retrieveAllTablesAndColumns(limit:number,offset:number): Observable<TablesWithColumns> {
    const url = `${this.baseUrl}/${limit}/${offset}`;
    return this.http.get<TablesWithColumns>(url);

  }
  paramTableTreemap(): Observable<any> {
    const url = `${this.baseUrl}/dashboard`;
    return this.http.get<any>(url);
  }

  getDataFromTable(table:TableInfo): Observable<any> {
    const columnsVar = Array.isArray(table.selectedColumns) && table.selectedColumns.length > 0
      ? table.selectedColumns.join(',')
      : '';
    const url = `${this.baseUrl}/${table.name}?columns=${columnsVar}&sortByColumn=${table.sortByColumn}&sortOrder=${table.sortOrder}&limit=${table.limit}&offset=${table.offset}&search=${table.search}`;

    return this.http.get<any[]>(url);}
    addInstance(instanceData: any, tableName: string) {
    const url = `${this.baseUrl}/${tableName}`;
    return this.http.post(url, instanceData, { responseType: 'text' });
  }
  checkunicity(primaryKeyvalue:string,tableName:string):Observable<boolean>{
    const url = `${this.baseUrl}/unicity/${tableName}/${primaryKeyvalue}`;
     return this.http.get<boolean>(url);

  }
  updateInstance(instanceData: any, tableName: string) {
    const url = `${this.baseUrl}/update/${tableName}`;
    return this.http.put(url, instanceData, { responseType: 'text' });
  }
  fkoptions(tableName :string):Observable<any[]> {
    const url = `${this.baseUrl}/${tableName}/fkoptions`;
    return this.http.get<any[]>(url,);
  }
cancelUpdateInstance(tableName:string,primaryKeyValue :string):Observable<any> {
    const url = `${this.baseUrl}/cancelupdate/${tableName}/${primaryKeyValue}`;
    return this.http.post<string>(url, {},);
  }

  paramHistory(tableName:string):Observable<ParamAudit[]> {
    const url = `${this.baseUrl}/${tableName}/history`;
    return this.http.get<ParamAudit[]>(url);
  }
checkreferences(tableName: string, primaryKeyValue: string): Observable<any[]>{
  const url = `${this.baseUrl}/${tableName}/references/${primaryKeyValue}`;
  return this.http.get<any[]>(url);
}

}
