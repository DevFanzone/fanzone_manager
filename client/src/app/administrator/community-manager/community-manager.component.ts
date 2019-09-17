import {Component, OnInit, ViewChild} from '@angular/core';
import {UsuarioModel} from "../../shared/models/usuario-model";
import {ModalComponent} from "ng2-bs4-modal/lib/components/modal";
import {FormGroup} from "@angular/forms";
import {UserService} from "../../shared/services/user.service";
import {Router} from "@angular/router";
import {NotifyService} from "../../shared/services/notify.service";

@Component({
    selector: 'app-community-manager',
    templateUrl: './community-manager.component.html',
    styleUrls: ['./community-manager.component.css']
})
export class CommunityManagerComponent implements OnInit {
    @ViewChild('modalRegister') modalRegister: ModalComponent;
    @ViewChild('formCm') formCm: FormGroup;
    public usuarioModel: UsuarioModel;
    public loaderActive: boolean = false;
    public isRequesting: boolean;
    public search: any;
    public data: any;
    public p: any;

    constructor(private toasty: NotifyService, private userService: UserService, private route: Router) {
        this.data = []
        this.usuarioModel = <UsuarioModel>{};
    }

    ngOnInit() {
        this.getUsuarios()
    }

    getUsuarios() {
        this.loaderActive = true;
        this.userService.getCms().subscribe(
            (res: any) => {
                this.data = res;

                if (!this.data)
                    this.data = []
            },
            error => {
                this.loaderActive = false;
            },
            () => {
                this.loaderActive = false;
            });
    }

    saveCm() {
        if (this.formCm.valid) {
            this.loaderActive = true;
            this.isRequesting = true;
            this.usuarioModel.role = 3
            this.userService.saveUsuario(this.usuarioModel).finally(() => this.isRequesting = false).subscribe(
                result => {
                    if (result.errorAPI) {
                        this.toasty.addToast("", result.mensajeAPI, "error");
                    }
                },
                errors => {
                    this.loaderActive = false;
                    this.toasty.addToast("", 'An error has occurred', "error");
                },
                () => {
                    this.loaderActive = false;
                    this.toasty.addToast("", 'The community manager have been saved.', "success");
                    this.getUsuarios()
                    this.closeForm();
                }
            )
        } else {
            for (let i in this.formCm.controls)
                this.formCm.controls[i].markAsTouched();
            this.toasty.addToast("", 'Some required fields are missing', "error");
        }
    }

    statusCm() {
        this.loaderActive = true;
        this.usuarioModel.status = this.usuarioModel.status == true ? false : true;
        this.userService.updateStatus(this.usuarioModel.id, this.usuarioModel.status)
            .finally(() => this.isRequesting = false)
            .subscribe(
                (result) => {
                },
                errors => {
                    this.loaderActive = false;
                    this.toasty.addToast("", 'An error has occurred', "error");
                },
                () => {
                    this.getUsuarios()
                }
            )
    }

    openForm(cm?) {
        if (cm) {
            this.usuarioModel = {...cm}
            this.modalRegister.open('lg');
        } else {
            this.modalRegister.open('lg');
        }
    }

    closeForm() {
        this.modalRegister.close();
        setTimeout(() => {
            this.usuarioModel = <UsuarioModel>{};
            this.formCm.reset()
        }, 500)
    }

    back() {
        this.route.navigate(['admin'])
    }
}
