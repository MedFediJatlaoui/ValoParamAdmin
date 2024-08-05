/**
 * OpenApi specification
 * OpenApi documentation for Admin Module Demo
 *
 * The version of the OpenAPI document: 1.0
 *
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
/* tslint:disable:no-unused-variable member-ordering */

import { Inject, Injectable, Optional } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams, HttpResponse, HttpEvent, HttpParameterCodec, HttpContext } from '@angular/common/http';
import { CustomHttpParameterCodec } from '../encoder';
import { Observable } from 'rxjs';

// @ts-ignore
import { CategoryDto } from '../model/categoryDto';

// @ts-ignore
import { BASE_PATH, COLLECTION_FORMATS } from '../variables';
import { Configuration } from '../configuration';

@Injectable({
  providedIn: 'root'
})
export class CategoryService {

  protected basePath = 'http://localhost:8090';
  public defaultHeaders = new HttpHeaders();
  public configuration = new Configuration();
  public encoder: HttpParameterCodec;

  constructor(protected httpClient: HttpClient, @Optional() @Inject(BASE_PATH) basePath: string | string[], @Optional() configuration: Configuration) {
    if (configuration) {
      this.configuration = configuration;
    }
    if (typeof this.configuration.basePath !== 'string') {
      if (Array.isArray(basePath) && basePath.length > 0) {
        basePath = basePath[0];
      }

      if (typeof basePath !== 'string') {
        basePath = this.basePath;
      }
      this.configuration.basePath = basePath;
    }
    this.encoder = this.configuration.encoder || new CustomHttpParameterCodec();
  }

  // @ts-ignore
  private addToHttpParams(httpParams: HttpParams, value: any, key?: string): HttpParams {
    if (typeof value === "object" && value instanceof Date === false) {
      httpParams = this.addToHttpParamsRecursive(httpParams, value);
    } else {
      httpParams = this.addToHttpParamsRecursive(httpParams, value, key);
    }
    return httpParams;
  }

  private addToHttpParamsRecursive(httpParams: HttpParams, value?: any, key?: string): HttpParams {
    if (value == null) {
      return httpParams;
    }

    if (typeof value === "object") {
      if (Array.isArray(value)) {
        (value as any[]).forEach(elem => httpParams = this.addToHttpParamsRecursive(httpParams, elem, key));
      } else if (value instanceof Date) {
        if (key != null) {
          httpParams = httpParams.append(key, (value as Date).toISOString().substring(0, 10));
        } else {
          throw Error("key may not be null if value is Date");
        }
      } else {
        Object.keys(value).forEach(k => httpParams = this.addToHttpParamsRecursive(
          httpParams, value[k], key != null ? `${key}.${k}` : k));
      }
    } else if (key != null) {
      httpParams = httpParams.append(key, value);
    } else {
      throw Error("key may not be null if value is not object or array");
    }
    return httpParams;
  }

  /**
   * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
   * @param reportProgress flag to report request and response progress.
   */
  public getAllCategories(observe?: 'body', reportProgress?: boolean, options?: { httpHeaderAccept?: '*/*', context?: HttpContext, transferCache?: boolean }): Observable<Array<CategoryDto>>;
  public getAllCategories(observe?: 'response', reportProgress?: boolean, options?: { httpHeaderAccept?: '*/*', context?: HttpContext, transferCache?: boolean }): Observable<HttpResponse<Array<CategoryDto>>>;
  public getAllCategories(observe?: 'events', reportProgress?: boolean, options?: { httpHeaderAccept?: '*/*', context?: HttpContext, transferCache?: boolean }): Observable<HttpEvent<Array<CategoryDto>>>;
  public getAllCategories(observe: any = 'body', reportProgress: boolean = false, options?: { httpHeaderAccept?: '*/*', context?: HttpContext, transferCache?: boolean }): Observable<any> {

    let localVarHeaders = this.defaultHeaders;

    let localVarCredential: string | undefined;
    // authentication (bearerAuth) required
    localVarCredential = this.configuration.lookupCredential('bearerAuth');
    if (localVarCredential) {
      localVarHeaders = localVarHeaders.set('Authorization', 'Bearer ' + localVarCredential);
    }

    let localVarHttpHeaderAcceptSelected: string | undefined = options && options.httpHeaderAccept;
    if (localVarHttpHeaderAcceptSelected === undefined) {
      // to determine the Accept header
      const httpHeaderAccepts: string[] = [
        '*/*'
      ];
      localVarHttpHeaderAcceptSelected = this.configuration.selectHeaderAccept(httpHeaderAccepts);
    }
    if (localVarHttpHeaderAcceptSelected !== undefined) {
      localVarHeaders = localVarHeaders.set('Accept', localVarHttpHeaderAcceptSelected);
    }

    let localVarHttpContext: HttpContext | undefined = options && options.context;
    if (localVarHttpContext === undefined) {
      localVarHttpContext = new HttpContext();
    }

    let localVarTransferCache: boolean | undefined = options && options.transferCache;
    if (localVarTransferCache === undefined) {
      localVarTransferCache = true;
    }

    let responseType_: 'text' | 'json' = 'json';
    if (localVarHttpHeaderAcceptSelected) {
      if (localVarHttpHeaderAcceptSelected.startsWith('text')) {
        responseType_ = 'text';
      } else if (this.configuration.isJsonMime(localVarHttpHeaderAcceptSelected)) {
        responseType_ = 'json';
      }
    }

    let localVarPath = `/api/categories`;
    return this.httpClient.request<Array<CategoryDto>>('get', `${this.configuration.basePath}${localVarPath}`,
      {
        context: localVarHttpContext,
        responseType: <any>responseType_,
        withCredentials: this.configuration.withCredentials,
        headers: localVarHeaders,
        observe: observe,
        reportProgress: reportProgress
      }
    );
  }

  /**
   * @param id
   * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
   * @param reportProgress flag to report request and response progress.
   */
  public getCategoryById(id: number, observe?: 'body', reportProgress?: boolean, options?: { httpHeaderAccept?: '*/*', context?: HttpContext, transferCache?: boolean }): Observable<CategoryDto>;
  public getCategoryById(id: number, observe?: 'response', reportProgress?: boolean, options?: { httpHeaderAccept?: '*/*', context?: HttpContext, transferCache?: boolean }): Observable<HttpResponse<CategoryDto>>;
  public getCategoryById(id: number, observe?: 'events', reportProgress?: boolean, options?: { httpHeaderAccept?: '*/*', context?: HttpContext, transferCache?: boolean }): Observable<HttpEvent<CategoryDto>>;
  public getCategoryById(id: number, observe: any = 'body', reportProgress: boolean = false, options?: { httpHeaderAccept?: '*/*', context?: HttpContext, transferCache?: boolean }): Observable<any> {
    if (id === null || id === undefined) {
      throw new Error('Required parameter id was null or undefined when calling getCategoryById.');
    }

    let localVarHeaders = this.defaultHeaders;

    let localVarCredential: string | undefined;
    // authentication (bearerAuth) required
    localVarCredential = this.configuration.lookupCredential('bearerAuth');
    if (localVarCredential) {
      localVarHeaders = localVarHeaders.set('Authorization', 'Bearer ' + localVarCredential);
    }

    let localVarHttpHeaderAcceptSelected: string | undefined = options && options.httpHeaderAccept;
    if (localVarHttpHeaderAcceptSelected === undefined) {
      // to determine the Accept header
      const httpHeaderAccepts: string[] = [
        '*/*'
      ];
      localVarHttpHeaderAcceptSelected = this.configuration.selectHeaderAccept(httpHeaderAccepts);
    }
    if (localVarHttpHeaderAcceptSelected !== undefined) {
      localVarHeaders = localVarHeaders.set('Accept', localVarHttpHeaderAcceptSelected);
    }

    let localVarHttpContext: HttpContext | undefined = options && options.context;
    if (localVarHttpContext === undefined) {
      localVarHttpContext = new HttpContext();
    }

    let localVarTransferCache: boolean | undefined = options && options.transferCache;
    if (localVarTransferCache === undefined) {
      localVarTransferCache = true;
    }

    let responseType_: 'text' | 'json' = 'json';
    if (localVarHttpHeaderAcceptSelected) {
      if (localVarHttpHeaderAcceptSelected.startsWith('text')) {
        responseType_ = 'text';
      } else if (this.configuration.isJsonMime(localVarHttpHeaderAcceptSelected)) {
        responseType_ = 'json';
      }
    }

    let localVarPath = `/api/categories/${this.configuration.encodeParam({ name: "id", value: id, in: "path", style: "simple", explode: false, dataType: "number", dataFormat: "int32" })}`;
    return this.httpClient.request<CategoryDto>('get', `${this.configuration.basePath}${localVarPath}`,
      {
        context: localVarHttpContext,
        responseType: <any>responseType_,
        withCredentials: this.configuration.withCredentials,
        headers: localVarHeaders,
        observe: observe,
        reportProgress: reportProgress
      }
    );
  }

  /**
   * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
   * @param reportProgress flag to report request and response progress.
   */
  public getTopUsedCategories(observe?: 'body', reportProgress?: boolean, options?: { httpHeaderAccept?: '*/*', context?: HttpContext, transferCache?: boolean }): Observable<Array<CategoryDto>>;
  public getTopUsedCategories(observe?: 'response', reportProgress?: boolean, options?: { httpHeaderAccept?: '*/*', context?: HttpContext, transferCache?: boolean }): Observable<HttpResponse<Array<CategoryDto>>>;
  public getTopUsedCategories(observe?: 'events', reportProgress?: boolean, options?: { httpHeaderAccept?: '*/*', context?: HttpContext, transferCache?: boolean }): Observable<HttpEvent<Array<CategoryDto>>>;
  public getTopUsedCategories(observe: any = 'body', reportProgress: boolean = false, options?: { httpHeaderAccept?: '*/*', context?: HttpContext, transferCache?: boolean }): Observable<any> {

    let localVarHeaders = this.defaultHeaders;

    let localVarCredential: string | undefined;
    // authentication (bearerAuth) required
    localVarCredential = this.configuration.lookupCredential('bearerAuth');
    if (localVarCredential) {
      localVarHeaders = localVarHeaders.set('Authorization', 'Bearer ' + localVarCredential);
    }

    let localVarHttpHeaderAcceptSelected: string | undefined = options && options.httpHeaderAccept;
    if (localVarHttpHeaderAcceptSelected === undefined) {
      // to determine the Accept header
      const httpHeaderAccepts: string[] = [
        '*/*'
      ];
      localVarHttpHeaderAcceptSelected = this.configuration.selectHeaderAccept(httpHeaderAccepts);
    }
    if (localVarHttpHeaderAcceptSelected !== undefined) {
      localVarHeaders = localVarHeaders.set('Accept', localVarHttpHeaderAcceptSelected);
    }

    let localVarHttpContext: HttpContext | undefined = options && options.context;
    if (localVarHttpContext === undefined) {
      localVarHttpContext = new HttpContext();
    }

    let localVarTransferCache: boolean | undefined = options && options.transferCache;
    if (localVarTransferCache === undefined) {
      localVarTransferCache = true;
    }

    let responseType_: 'text' | 'json' = 'json';
    if (localVarHttpHeaderAcceptSelected) {
      if (localVarHttpHeaderAcceptSelected.startsWith('text')) {
        responseType_ = 'text';
      } else if (this.configuration.isJsonMime(localVarHttpHeaderAcceptSelected)) {
        responseType_ = 'json';
      }
    }

    let localVarPath = `/api/categories/top-used`;
    return this.httpClient.request<Array<CategoryDto>>('get', `${this.configuration.basePath}${localVarPath}`,
      {
        context: localVarHttpContext,
        responseType: <any>responseType_,
        withCredentials: this.configuration.withCredentials,
        headers: localVarHeaders,
        observe: observe,
        reportProgress: reportProgress
      }
    );
  }
  /**
   * Update an existing category
   * @param id The ID of the category to update
   * @param categoryDto The updated category data
   * @param observe Set whether or not to return the data Observable as the body, response or events. Defaults to returning the body.
   * @param reportProgress Flag to report request and response progress.
   */
  public updateCategory(id: number, categoryDto: CategoryDto, observe?: 'body', reportProgress?: boolean, options?: { httpHeaderAccept?: '*/*', context?: HttpContext, transferCache?: boolean }): Observable<CategoryDto>;
  public updateCategory(id: number, categoryDto: CategoryDto, observe?: 'response', reportProgress?: boolean, options?: { httpHeaderAccept?: '*/*', context?: HttpContext, transferCache?: boolean }): Observable<HttpResponse<CategoryDto>>;
  public updateCategory(id: number, categoryDto: CategoryDto, observe?: 'events', reportProgress?: boolean, options?: { httpHeaderAccept?: '*/*', context?: HttpContext, transferCache?: boolean }): Observable<HttpEvent<CategoryDto>>;
  public updateCategory(id: number, categoryDto: CategoryDto, observe: any = 'body', reportProgress: boolean = false, options?: { httpHeaderAccept?: '*/*', context?: HttpContext, transferCache?: boolean }): Observable<any> {
    if (id === null || id === undefined) {
      throw new Error('Required parameter id was null or undefined when calling updateCategory.');
    }
    if (categoryDto === null || categoryDto === undefined) {
      throw new Error('Required parameter categoryDto was null or undefined when calling updateCategory.');
    }

    let localVarHeaders = this.defaultHeaders;

    let localVarCredential: string | undefined;
    // authentication (bearerAuth) required
    localVarCredential = this.configuration.lookupCredential('bearerAuth');
    if (localVarCredential) {
      localVarHeaders = localVarHeaders.set('Authorization', 'Bearer ' + localVarCredential);
    }

    let localVarHttpHeaderAcceptSelected: string | undefined = options && options.httpHeaderAccept;
    if (localVarHttpHeaderAcceptSelected === undefined) {
      // to determine the Accept header
      const httpHeaderAccepts: string[] = [
        '*/*'
      ];
      localVarHttpHeaderAcceptSelected = this.configuration.selectHeaderAccept(httpHeaderAccepts);
    }
    if (localVarHttpHeaderAcceptSelected !== undefined) {
      localVarHeaders = localVarHeaders.set('Accept', localVarHttpHeaderAcceptSelected);
    }

    let localVarHttpContext: HttpContext | undefined = options && options.context;
    if (localVarHttpContext === undefined) {
      localVarHttpContext = new HttpContext();
    }

    let localVarTransferCache: boolean | undefined = options && options.transferCache;
    if (localVarTransferCache === undefined) {
      localVarTransferCache = true;
    }

    let responseType_: 'text' | 'json' = 'json';
    if (localVarHttpHeaderAcceptSelected) {
      if (localVarHttpHeaderAcceptSelected.startsWith('text')) {
        responseType_ = 'text';
      } else if (this.configuration.isJsonMime(localVarHttpHeaderAcceptSelected)) {
        responseType_ = 'json';
      }
    }

    let localVarPath = `/api/categories/${this.configuration.encodeParam({ name: "id", value: id, in: "path", style: "simple", explode: false, dataType: "number", dataFormat: "int32" })}`;
    return this.httpClient.request<CategoryDto>('put', `${this.configuration.basePath}${localVarPath}`, {
      body: categoryDto,
      context: localVarHttpContext,
      responseType: <any>responseType_,
      withCredentials: this.configuration.withCredentials,
      headers: localVarHeaders,
      observe: observe,
      reportProgress: reportProgress
    });
  }
  /**
   * @param categoryId The ID of the category to reference
   * @param observe Set whether or not to return the data Observable as the body, response, or events. Defaults to returning the body.
   * @param reportProgress Flag to report request and response progress.
   */
  public refCategory(categoryId: number, observe?: 'body', reportProgress?: boolean, options?: { httpHeaderAccept?: '*/*', context?: HttpContext, transferCache?: boolean }): Observable<number>;
  public refCategory(categoryId: number, observe?: 'response', reportProgress?: boolean, options?: { httpHeaderAccept?: '*/*', context?: HttpContext, transferCache?: boolean }): Observable<HttpResponse<number>>;
  public refCategory(categoryId: number, observe?: 'events', reportProgress?: boolean, options?: { httpHeaderAccept?: '*/*', context?: HttpContext, transferCache?: boolean }): Observable<HttpEvent<number>>;
  public refCategory(categoryId: number, observe: any = 'body', reportProgress: boolean = false, options?: { httpHeaderAccept?: '*/*', context?: HttpContext, transferCache?: boolean }): Observable<any> {
    if (categoryId === null || categoryId === undefined) {
      throw new Error('Required parameter categoryId was null or undefined when calling refCategory.');
    }

    let localVarHeaders = this.defaultHeaders;

    let localVarCredential: string | undefined;
    // authentication (bearerAuth) required
    localVarCredential = this.configuration.lookupCredential('bearerAuth');
    if (localVarCredential) {
      localVarHeaders = localVarHeaders.set('Authorization', 'Bearer ' + localVarCredential);
    }

    let localVarHttpHeaderAcceptSelected: string | undefined = options && options.httpHeaderAccept;
    if (localVarHttpHeaderAcceptSelected === undefined) {
      // to determine the Accept header
      const httpHeaderAccepts: string[] = [
        '*/*'
      ];
      localVarHttpHeaderAcceptSelected = this.configuration.selectHeaderAccept(httpHeaderAccepts);
    }
    if (localVarHttpHeaderAcceptSelected !== undefined) {
      localVarHeaders = localVarHeaders.set('Accept', localVarHttpHeaderAcceptSelected);
    }

    let localVarHttpContext: HttpContext | undefined = options && options.context;
    if (localVarHttpContext === undefined) {
      localVarHttpContext = new HttpContext();
    }

    let localVarTransferCache: boolean | undefined = options && options.transferCache;
    if (localVarTransferCache === undefined) {
      localVarTransferCache = true;
    }

    let responseType_: 'text' | 'json' = 'json';
    if (localVarHttpHeaderAcceptSelected) {
      if (localVarHttpHeaderAcceptSelected.startsWith('text')) {
        responseType_ = 'text';
      } else if (this.configuration.isJsonMime(localVarHttpHeaderAcceptSelected)) {
        responseType_ = 'json';
      }
    }

    let localVarPath = `/api/categories/ref/${encodeURIComponent(String(categoryId))}`;
    return this.httpClient.request<number>('get', `${this.basePath}${localVarPath}`,
      {
        context: localVarHttpContext,
        responseType: <any>responseType_,
        withCredentials: this.configuration.withCredentials,
        headers: localVarHeaders,
        observe: observe,
        reportProgress: reportProgress
      }
    );
  }
  /**
   * Delete a category by its ID
   * @param categoryId The ID of the category to delete
   * @param observe Set whether or not to return the data Observable as the body, response, or events. Defaults to returning the body.
   * @param reportProgress Flag to report request and response progress.
   */
  public deleteCategory(categoryId: number, observe?: 'body', reportProgress?: boolean, options?: { httpHeaderAccept?: '*/*', context?: HttpContext, transferCache?: boolean }): Observable<any>;
  public deleteCategory(categoryId: number, observe?: 'response', reportProgress?: boolean, options?: { httpHeaderAccept?: '*/*', context?: HttpContext, transferCache?: boolean }): Observable<HttpResponse<any>>;
  public deleteCategory(categoryId: number, observe?: 'events', reportProgress?: boolean, options?: { httpHeaderAccept?: '*/*', context?: HttpContext, transferCache?: boolean }): Observable<HttpEvent<any>>;
  public deleteCategory(categoryId: number, observe: any = 'body', reportProgress: boolean = false, options?: { httpHeaderAccept?: '*/*', context?: HttpContext, transferCache?: boolean }): Observable<any> {
    if (categoryId === null || categoryId === undefined) {
      throw new Error('Required parameter categoryId was null or undefined when calling deleteCategory.');
    }

    let localVarHeaders = this.defaultHeaders;

    let localVarCredential: string | undefined;
    // authentication (bearerAuth) required
    localVarCredential = this.configuration.lookupCredential('bearerAuth');
    if (localVarCredential) {
      localVarHeaders = localVarHeaders.set('Authorization', 'Bearer ' + localVarCredential);
    }

    let localVarHttpHeaderAcceptSelected: string | undefined = options && options.httpHeaderAccept;
    if (localVarHttpHeaderAcceptSelected === undefined) {
      // to determine the Accept header
      const httpHeaderAccepts: string[] = [
        '*/*'
      ];
      localVarHttpHeaderAcceptSelected = this.configuration.selectHeaderAccept(httpHeaderAccepts);
    }
    if (localVarHttpHeaderAcceptSelected !== undefined) {
      localVarHeaders = localVarHeaders.set('Accept', localVarHttpHeaderAcceptSelected);
    }

    let localVarHttpContext: HttpContext | undefined = options && options.context;
    if (localVarHttpContext === undefined) {
      localVarHttpContext = new HttpContext();
    }

    let localVarTransferCache: boolean | undefined = options && options.transferCache;
    if (localVarTransferCache === undefined) {
      localVarTransferCache = true;
    }

    let responseType_: 'text' | 'json' = 'json';
    if (localVarHttpHeaderAcceptSelected) {
      if (localVarHttpHeaderAcceptSelected.startsWith('text')) {
        responseType_ = 'text';
      } else if (this.configuration.isJsonMime(localVarHttpHeaderAcceptSelected)) {
        responseType_ = 'json';
      }
    }

    let localVarPath = `/api/categories/delete/${encodeURIComponent(String(categoryId))}`;
    return this.httpClient.request<any>('get', `${this.configuration.basePath}${localVarPath}`,
      {
        context: localVarHttpContext,
        responseType: <any>responseType_,
        withCredentials: this.configuration.withCredentials,
        headers: localVarHeaders,
        observe: observe,
        reportProgress: reportProgress
      }
    );
  }


}
