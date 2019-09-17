import {Component, OnInit, ViewChild} from '@angular/core';
import {CategoryService} from "../../../shared/services/category.service";
import {Router} from "@angular/router";
import {ModalComponent} from "ng2-bs4-modal/lib/components/modal";
import {FormGroup} from "@angular/forms";
import {FileItem, FileUploader, FileUploaderOptions, ParsedResponseHeaders} from "ng2-file-upload";
import {Categoria} from "../../../shared/models/categoria";
import {ConfigService} from "../../../shared/utils/config.service";
import {NotifyService} from "../../../shared/services/notify.service";

@Component({
    selector: 'app-categories',
    templateUrl: './categories.component.html',
    styleUrls: ['./categories.component.css']
})
export class CategoriesComponent implements OnInit {
    @ViewChild('modalRegister') modalRegister: ModalComponent;
    @ViewChild('confirmation') confirmation: ModalComponent;
    @ViewChild('formCategory') formCategory: FormGroup;
    private baseUrl: string;
    public successMensaje: string;
    public username: string;
    public file: FileList;
    public uploader: FileUploader;
    public isRequesting: boolean;
    public loaderActive: boolean = false;
    public search: any;
    public player: any;
    public data: any;
    public p: any;
    public categoryModel: Categoria;
    public isEditFoto: boolean = false;
    public isEditSplash: boolean = false;
    public idCategoryDelete: any;

    constructor(private toasty: NotifyService, private categoryService: CategoryService, private configService: ConfigService, private route: Router) {
        this.data = []
        this.player = JSON.parse(localStorage.getItem('player'))

        if (!this.player) {
            this.route.navigate(['admin/main'])
        }

        this.categoryModel = <Categoria>{};
        this.categoryModel.imagen = ""
        this.categoryModel.id = "0";
        this.categoryModel.imagenBytes = "noBytes";
        this.categoryModel.imgSplash = 'noimg.jpg';
        this.categoryModel.imgSplashBytes = 'noBytes';
        this.categoryModel.lastUpdateSplash = "";

        this.baseUrl = configService.getApiURI();
        this.uploader = new FileUploader({url: this.baseUrl + "/categoria", itemAlias: "archivo"});
    }

    ngOnInit() {
        this.getCategories()
    }

    getCategories() {
        this.loaderActive = true;
        this.categoryService.getCategorias(this.player.id)
            .finally(() => this.isRequesting = false)
            .subscribe((result) => {
                    if (result.errorAPI) {
                        this.data = []
                        this.loaderActive = false;
                    } else {
                        this.loaderActive = false;
                        this.data = result.categorias;
                    }
                },
                errors => {
                    this.loaderActive = false;
                }
            )
    }

    saveCategory() {
        this.categoryModel.username = "";
        this.categoryModel.username = localStorage.getItem('username');
        if (this.categoryModel.id == "0") {
            if (this.categoryModel.imgSplashBytes == "noBytes" || this.categoryModel.imagenBytes == "noBytes" || this.formCategory.invalid) {
                this.toasty.addToast("", 'Some required fields are missing', "error");
                for (let i in this.formCategory.controls)
                    this.formCategory.controls[i].markAsTouched();
            } else {
                this.loaderActive = true;
                this.categoryModel.idAtleta = this.player.id
                this.categoryService.guardarCategoria(this.categoryModel)
                    .finally(() => this.isRequesting = false)
                    .subscribe(
                        (result) => {
                            if (result.errorAPI) {
                                this.loaderActive = false;
                                this.toasty.addToast("", 'An error has ocurred', "error");
                            } else {
                                this.loaderActive = false;
                                this.categoryModel.imagenBytes = "noBytes";
                                this.categoryModel.imgSplashBytes = "noBytes";
                                this.toasty.addToast("", 'The category have been saved.', "success");
                                this.getCategories()
                                this.closeForm()
                            }
                        },
                        errors => {
                            this.loaderActive = false;
                            this.toasty.addToast("", 'An error has occurred', "error");
                        }
                    )
            }
        } else {
            if (this.formCategory.invalid) {
                this.toasty.addToast("", 'Some required fields are missing', "error");
            } else {
                this.loaderActive = true;
                this.categoryService.guardarCategoria(this.categoryModel)
                    .finally(() => this.isRequesting = false)
                    .subscribe(
                        (result) => {
                            if (result.errorAPI) {
                                this.loaderActive = false;
                                this.toasty.addToast("", 'An error has occurred', "error");
                            } else {
                                this.loaderActive = false;
                                this.toasty.addToast("", 'The category have been modified.', "success");
                                this.getCategories()
                                this.closeForm()
                            }
                        },
                        errors => {
                            this.loaderActive = false;
                            this.toasty.addToast("", 'An error has occurred', "error");
                        }
                    )

            }
        }
    }

    statusCategory() {
        this.categoryModel.estatus = this.categoryModel.estatus == '0' ? '1' : '0'
        if (this.categoryModel.estatus == '0') {
            this.categoryService.activateCategoria(this.categoryModel.id)
                .finally(() => this.isRequesting = false)
                .subscribe(
                    (result) => {
                        if (result) {
                            this.getCategories()
                        }
                    },
                    errors => {
                        this.toasty.addToast("", 'An error has occurred', "error");
                    }
                )
        } else {
            this.categoryService.desactivarCategoria(this.categoryModel.id)
                .finally(() => this.isRequesting = false)
                .subscribe(
                    (result) => {
                        if (result) {
                            this.getCategories()
                        }
                    },
                    errors => {
                        this.toasty.addToast("", 'An error has occurred', "error");
                    }
                )
        }
    }

    openDeleteCategory(category) {
        this.idCategoryDelete = category.id;
        this.successMensaje = "The category " + category.nombre + " and the elements that contains (photos and videos) will be deleted permanently, do you wish to procede?"
        this.confirmation.open()
    }

    confirmationDeleteCategory() {
        this.confirmation.close();
        this.categoryService.deleteCategoria(this.idCategoryDelete)
            .finally(() => this.isRequesting = false)
            .subscribe((result) => {
                },
                errors => {
                    this.toasty.addToast("", 'An error has occurred', "error");
                },
                () => {
                    this.toasty.addToast("", 'The category have been deleted.', "success");
                    this.getCategories();
                }
            )
    }

    cancelDeleteCategory() {
        this.idCategoryDelete = 0;
        this.successMensaje = '';
        this.confirmation.close();
    }

    onchangueFile(fileInput, splash) {
        if (fileInput.target.files && fileInput.target.files[0]) {
            this.file = fileInput.target.files;
            const reader = new FileReader();
            reader.onload = ((e) => {
                if (this.file[0].type == "image/jpeg" || this.file[0].type == "image/png") {
                    if (splash) {
                        var image = new Image();
                        image.src = e.target['result'];
                        image.onload = () => {
                            if (image.height < 2732 || image.width < 2048) {
                                this.toasty.addToast('', 'The height and width should be greater 2048x2732.', 'info')
                            } else {
                                this.categoryModel.imgSplash = '';
                                this.categoryModel.imgSplashBytes = '';
                                this.categoryModel.lastUpdateSplash = null;
                                this.categoryModel.imgSplashType = '';
                                this.categoryModel.imgSplash = e.target['result'];
                                this.categoryModel.imgSplashBytes = e.target['result'];
                                this.categoryModel.lastUpdateSplash = Date.now();
                                this.categoryModel.imgSplashType = this.file[0].type;
                                this.isEditSplash = true;
                            }
                        }
                    } else {
                        var image = new Image();
                        image.src = e.target['result'];
                        image.onload = () => {
                            if (image.height < 207 || image.width < 233) {
                                this.toasty.addToast('', 'The height and width should be greater 233×207.', 'info')
                            } else {
                                this.categoryModel.imagen = '';
                                this.categoryModel.imagenBytes = '';
                                this.categoryModel.imagenType = '';
                                this.categoryModel.imagen = e.target['result'];
                                this.categoryModel.imagenBytes = e.target['result'];
                                this.categoryModel.imagenType = this.file[0].type
                                this.isEditFoto = true;
                            }
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

    openForm(category?) {
        if (category) {
            this.categoryModel = {...category}
            this.isEditFoto = true;
            this.isEditSplash = true;
            this.categoryModel.imagenBytes = "noBytes";
            this.categoryModel.imgSplashBytes = "noBytes";
            this.modalRegister.open('lg');
        } else {
            this.modalRegister.open('lg');
        }
    }

    closeForm() {
        this.modalRegister.close();
        this.categoryModel = <Categoria>{};
        (<HTMLInputElement>document.getElementById('file-upload')).value = '';
        (<HTMLInputElement>document.getElementById('file-upload-splash')).value = '';
        this.categoryModel.imagen = '';
        this.categoryModel.id = "0";
        this.categoryModel.imagenBytes = "noBytes";
        this.categoryModel.imgSplash = 'noimg.jpg';
        this.categoryModel.imgSplashBytes = 'noBytes';
        this.categoryModel.lastUpdateSplash = "";
        this.isEditFoto = false;
        this.isEditSplash = false;
        this.formCategory.reset()
    }

    back() {
        this.route.navigate(['admin/players'])
    }
}
