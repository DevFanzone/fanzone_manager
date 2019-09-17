import { Injectable } from '@angular/core';

import {ToastyService, ToastyConfig, ToastOptions, ToastData} from 'ng2-toasty';

@Injectable()
export class NotifyService {

  constructor(private toastyService:ToastyService, private toastyConfig: ToastyConfig) {
      toastyConfig.limit = 1
  }

  addToast(title,message,type) {

        var toastOptions:ToastOptions = {
            title: title,
            msg: message,
            showClose: true,
            timeout: 2000,
            theme: "bootstrap",
            onAdd: (toast:ToastData) => {},
            onRemove: function(toast:ToastData) {}
        };

        switch(type){
        	case 'info': this.toastyService.info(toastOptions); break;
        	case 'wait': this.toastyService.wait(toastOptions); break;
        	case 'success': this.toastyService.success(toastOptions); break;
        	case 'error': this.toastyService.error(toastOptions); break;
        	case 'warning': this.toastyService.warning(toastOptions); break;
        	case 'default': this.toastyService.default(toastOptions); break;
        }


  }

}
