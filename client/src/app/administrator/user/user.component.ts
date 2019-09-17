import {Component, OnInit, ViewChild} from '@angular/core';
import {ModalComponent} from "ng2-bs4-modal/lib/components/modal";
import {FormGroup} from "@angular/forms";
import {UserService} from "../../shared/services/user.service";
import {Router} from "@angular/router";
import {NotifyService} from "../../shared/services/notify.service";

@Component({
    selector: 'app-user',
    templateUrl: './user.component.html',
    styleUrls: ['./user.component.css']
})
export class UserComponent implements OnInit {
    @ViewChild('modalRegister') modalRegister: ModalComponent;
    @ViewChild('formUser') formUser: FormGroup;
    public usuarioModel: any;
    public loaderActive: boolean = false;
    public isRequesting: boolean;
    public search: any;
    public data: any;
    public roles: any;
    public p: any;

    constructor(private toasty: NotifyService, private userService: UserService, private route: Router) {
        this.data = []
        this.usuarioModel = {};
    }

    ngOnInit() {
        this.getUsers()
        this.getRoles()
    }

    getRoles() {
        this.userService.getRoles()
            .subscribe((data: any) => {
                    this.roles = data;
                },
                error => {
                });
    }

    getUsers() {
        this.loaderActive = true;
        this.userService.getUsers().subscribe(
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

    updateAccount() {
        if (this.formUser.valid) {
            this.loaderActive = true;
            this.isRequesting = true;

            this.userService.updateAccountUser(this.usuarioModel).finally(() => this.isRequesting = false).subscribe(
                result => {
                    if (result.errorAPI) {
                        this.toasty.addToast("", 'An error has occurred.', "error");
                    }
                },
                errors => {
                    this.toasty.addToast("", 'An error has occurred.', "error");
                    this.loaderActive = false;
                },
                () => {
                    this.toasty.addToast("", 'The user have been modified.', "success");
                    this.loaderActive = false;
                    this.getUsers()
                    this.closeForm();
                }
            )
        } else {
            for (let i in this.formUser.controls)
                this.formUser.controls[i].markAsTouched();
            this.toasty.addToast("", 'Some required fields are missing', "error");
        }
    }

    statusUser() {
        this.loaderActive = true;
        this.usuarioModel.status = this.usuarioModel.status == true ? false : true;
        this.userService.updateStatus(this.usuarioModel.id, this.usuarioModel.status)
            .finally(() => this.isRequesting = false)
            .subscribe(
                (result) => {
                },
                errors => {
                    this.loaderActive = false;
                    this.toasty.addToast("", 'An error has occurred.', "error");
                },
                () => {
                    this.getUsers()
                }
            )
    }

    openForm(user?) {
        if (user) {
            this.usuarioModel = {...user}
            this.modalRegister.open('lg');
        } else {
            this.modalRegister.open('lg');
        }
    }

    closeForm() {
        this.modalRegister.close();
        setTimeout(() => {
            this.usuarioModel = {};
            this.formUser.reset()
        }, 500)
    }

    back() {
        this.route.navigate(['admin'])
    }
}
