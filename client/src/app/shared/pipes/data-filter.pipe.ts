import * as _ from "lodash";
import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'dataFilter'
})
export class DataFilterPipe implements PipeTransform {

  transform(array: any[], query: string, campo: string): any {        
    if (query) {
      return _.filter(array, row => row[campo].indexOf(query.toUpperCase()) > -1);
    }
    return array;
  }

}