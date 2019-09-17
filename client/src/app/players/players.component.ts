import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {ModalComponent} from "ng2-bs4-modal/lib/components/modal";
import {PlayerService} from "../shared/services/player.service";
import {Router} from "@angular/router";
import {AtletaModel} from "../shared/models/atleta-model";
import {FileItem, FileUploader, FileUploaderOptions, ParsedResponseHeaders} from "ng2-file-upload";
import {FormGroup} from "@angular/forms";
import {ConfigService} from "../shared/utils/config.service";
import {NotifyService} from "../shared/services/notify.service";

@Component({
    selector: 'app-players',
    templateUrl: './players.component.html',
    styleUrls: ['./players.component.css']
})
export class PlayersComponent implements OnInit {
    @ViewChild('modalRegister') modalRegister: ModalComponent;
    @ViewChild('formPlayer') formPlayer: FormGroup;
    @ViewChild('filePost') filePost: ElementRef;
    private baseUrl: string;

    public role: string;
    public username: string;
    public isRequesting: boolean;
    public file: FileList;
    public uploader: FileUploader;
    public loaderActive: boolean = false;
    public isEditFoto: boolean = false;
    public atletaModel: AtletaModel;
    public data: any;
    public p: any;
    public cms: any;
    public arraySexo: any;
    public search: any;

    constructor(private toasty: NotifyService, private atletasService: PlayerService, private configService: ConfigService, private route: Router) {
        this.data = []
        this.cms = []
        this.role = localStorage.getItem('auth_role')
        this.atletaModel = <AtletaModel>{};
        this.atletaModel.id = "0";
        this.atletaModel.fotoBytes = "noBytes";
        this.arraySexo = [{id: "1", nombre: "Male"}, {id: "2", nombre: "Female"}];
        this.baseUrl = configService.getApiURI();
        this.uploader = new FileUploader({url: this.baseUrl + "/uploadFile", itemAlias: "archivo"});
    }

    ngOnInit() {
        localStorage.removeItem('player')
        this.getCMs()
        this.getAtletas();
    }

    getCMs() {
        this.atletasService.getCMs()
            .finally(() => this.isRequesting = false)
            .subscribe(
                (result) => {
                    if (result) {
                        if (result.errorAPI) {
                            this.loaderActive = false;
                        } else {
                            this.loaderActive = false;
                            this.cms = result.usuarios;
                        }
                    }
                },
                errors => {
                    this.loaderActive = false;
                    this.toasty.addToast("", 'An error has occurred', "error");
                }
            )
    }

    getAtletas() {
        this.loaderActive = true;
        this.username = "";
        this.username = localStorage.getItem('username');
        this.atletasService.getAtletas(this.username)
            .finally(() => this.isRequesting = false)
            .subscribe(
                (result) => {
                    if (result.errorAPI) {
                        this.loaderActive = false;
                    } else {
                        this.loaderActive = false;
                        this.data = result.atletas;
                    }

                },
                errors => {
                    this.loaderActive = false;
                    this.toasty.addToast("", 'An error has occurred', "error");
                }
            )
    }

    searchUsername() {
        for (let cm of this.cms) {
            if (cm.id == this.atletaModel.idManager) {
                this.atletaModel.username = cm.username
            }
        }
    }

    saveUsuario() {
        if (this.atletaModel.id == "0") {
            if (this.atletaModel.fotoBytes == "noBytes" || this.formPlayer.invalid) {
                for (let i in this.formPlayer.controls)
                    this.formPlayer.controls[i].markAsTouched();
                this.toasty.addToast("", 'Some required fields are missing', "error");
            } else {
                this.loaderActive = true;
                this.atletasService.guardarAtleta(this.atletaModel)
                    .finally(() => this.isRequesting = false)
                    .subscribe(
                        (result) => {
                            if (result.errorAPI) {
                                this.loaderActive = false;
                                this.toasty.addToast("", 'An error has occurred', "error");
                            } else {
                                this.getAtletas();
                                this.loaderActive = false;
                                this.formPlayer.reset()
                                this.filePost.nativeElement.value = "";
                                this.toasty.addToast("", 'The player have been saved.', "success");
                                this.closeForm()
                            }
                        },
                        errors => {
                            this.loaderActive = false;
                            this.toasty.addToast("", 'An error has occurred', "error");
                        })
            }
        } else {
            if (this.formPlayer.invalid) {
                for (let i in this.formPlayer.controls)
                    this.formPlayer.controls[i].markAsTouched();
                this.toasty.addToast("", 'Some required fields are missing', "error");
            } else {
                this.loaderActive = true;
                this.atletasService.guardarAtleta(this.atletaModel)
                    .finally(() => this.isRequesting = false)
                    .subscribe(
                        (result) => {
                            if (result.errorAPI) {
                                this.loaderActive = false;
                                this.toasty.addToast("", 'An error has occurred', "error");
                            } else {
                                this.toasty.addToast("", 'The player have been modified.', "success");
                                this.getAtletas();
                                this.loaderActive = false;
                                this.closeForm()
                            }
                        },
                        errors => {
                            this.loaderActive = false;
                            this.toasty.addToast("", 'An error has occurred', "error");
                        })
            }
        }
    }

    statusAtleta() {
        this.loaderActive = true;
        this.atletaModel.estatus = this.atletaModel.estatus == '0' ? '1' : '0';
        this.atletasService.updateStatus(this.atletaModel.id, this.atletaModel.estatus)
            .finally(() => this.isRequesting = false)
            .subscribe(
                (result) => {
                },
                errors => {
                    this.loaderActive = false;
                    this.toasty.addToast("", 'An error has occurred', "error");
                },
                () => {
                    this.getAtletas()
                }
            )
    }

    onchangueFile(fileInput) {
        if (fileInput.target.files && fileInput.target.files[0]) {
            this.file = fileInput.target.files;
            const reader = new FileReader();
            reader.onload = ((e) => {
                if (this.file[0].type == "image/jpeg" || this.file[0].type == "image/png") {
                    var image = new Image();
                    image.src = e.target['result'];
                    image.onload = () => {
                        if (image.height < 689 || image.width < 900) {
                            this.toasty.addToast('', 'The height and width should be greater 900×689.', 'info')
                        } else {
                            this.atletaModel.fotoType = this.file[0].type
                            this.atletaModel.foto = e.target['result'];
                            this.atletaModel.fotoBytes = e.target['result'];
                            this.isEditFoto = true;
                        }
                    }
                } else {
                    this.toasty.addToast("", 'File format not allowed', "info");
                }
            });
            reader.readAsDataURL(fileInput.target.files[0]);
        }
    }

    onSuccessItem(item: FileItem, response: string, status: number, headers: ParsedResponseHeaders): any {
        let exit = JSON.parse(response);
        this.uploader.progress = 0;
        this.uploader.clearQueue();
    }

    onErrorItem(item: FileItem, response: string, status: number, headers: ParsedResponseHeaders): any {
        let error = JSON.parse(response);
        this.uploader.clearQueue();
    }

    openForm(player?) {
        if (player) {
            this.atletaModel = {...player}
            this.isEditFoto = true;
            this.atletaModel.fotoBytes = "noBytes";
            this.modalRegister.open('lg');
        } else {
            this.modalRegister.open('lg');
        }
    }

    closeForm() {
        this.modalRegister.close();

        this.atletaModel = <AtletaModel>{};
        this.filePost.nativeElement.value = "";
        this.atletaModel.id = "0";
        this.atletaModel.fotoBytes = "noBytes";
        this.isEditFoto = false;
        this.formPlayer.reset()
    }

    goToPlayer(player) {
        localStorage.setItem('player', JSON.stringify(player))
        if (this.role == 'ROLE_ADMINISTRADOR') {
            this.route.navigate(['admin/player'])
        } else {
            this.route.navigate(['cm/post'])
        }
    }

    back() {
        if (this.role == 'ROLE_ADMINISTRADOR') {
            this.route.navigate(['admin'])
        }
    }
}
