import {Component, OnInit, ViewChild} from '@angular/core';
import {SliderService} from "../../../shared/services/slider.service";
import {Router} from "@angular/router";
import {ModalComponent} from "ng2-bs4-modal/lib/components/modal";
import {FormGroup} from "@angular/forms";
import {Slider} from "../../../shared/models/slider";
import {NotifyService} from "../../../shared/services/notify.service";

@Component({
    selector: 'app-sliders',
    templateUrl: './sliders.component.html',
    styleUrls: ['./sliders.component.css']
})
export class SlidersComponent implements OnInit {
    @ViewChild('modalRegister') modalRegister: ModalComponent;
    @ViewChild('confirmation') confirmation: ModalComponent;
    @ViewChild('formSlider') formSlider: FormGroup;

    public successMensaje: string;
    public username: string;
    public file: FileList;
    public fileAux: FileList;
    public arrayElementos = [];
    public numberSliders: number = 0;
    public isRequesting: boolean;
    public loaderActive: boolean = false;
    public search: any;
    public player: any;
    public data: any;
    public p: any;
    public sliderModel: Slider;
    public idSliderDelete: any;

    constructor(private toasty: NotifyService, private sliderService: SliderService, private route: Router) {
        this.data = []
        this.player = JSON.parse(localStorage.getItem('player'))

        if (!this.player) {
            this.route.navigate(['admin/main'])
        }
    }

    ngOnInit() {
        this.sliderModel = <Slider>{};
        this.sliderModel.idAtleta = this.player.id;
        this.getSliders()
    }

    getSliders() {
        this.loaderActive = true;
        this.sliderService.getSliders(this.player.id)
            .finally(() => this.isRequesting = false)
            .subscribe(
                (result) => {
                    if (result.errorAPI) {
                        this.data = []
                        this.loaderActive = false;
                    } else {
                        this.loaderActive = false;
                        this.data = result.sliders;
                    }

                },
                errors => {
                    this.loaderActive = false;
                }
            )
    }

    saveSliders() {
        this.sliderModel.username = "";
        this.sliderModel.username = localStorage.getItem('username');
        if (this.sliderModel.username == "" || this.arrayElementos.length <= 0) {
            this.loaderActive = false;
            this.toasty.addToast("", 'Some required fields are missing', "error");
        } else {
            this.loaderActive = true;
            this.sliderService.saveSlider(this.arrayElementos.length, this.sliderModel, this.arrayElementos)
                .finally(() => this.isRequesting = false)
                .subscribe(
                    (result) => {
                        if (result.errorAPI) {
                            setTimeout(() => {
                                this.loaderActive = false;
                                this.toasty.addToast("", 'An error has occurred.', "error");
                            }, 3000);
                        } else {
                            setTimeout(() => {
                                this.loaderActive = false;
                                this.toasty.addToast("", 'The sliders have been saved', "success");
                                this.closeForm()
                                this.getSliders()
                            }, 3000);
                        }
                    },
                    errors => {
                        setTimeout(() => {
                            this.loaderActive = false;
                            this.toasty.addToast("", 'An error has occurred.', "error");
                        }, 3000);
                    }
                )
        }
    }

    updateSlider() {
        this.loaderActive = true;
        this.sliderModel.estatus = this.sliderModel.estatus == '0' ? '1' : '0'
        this.sliderService.updateSlider(this.sliderModel.id, this.sliderModel.estatus)
            .finally(() => this.isRequesting = false)
            .subscribe(
                (result) => {
                },
                errors => {
                    this.loaderActive = false;
                    this.toasty.addToast("", 'An error has occurred.', "error");
                },
                () => {
                    let text = this.sliderModel.estatus == '0' ? 'enabled' : 'disabled'

                    this.toasty.addToast("", 'The sliders have been ' + text, "success");
                    this.getSliders()
                }
            )
    }

    openDeleteSlider(slider) {
        this.idSliderDelete = slider.id;
        this.successMensaje = "The slider " + slider.nombre + "  will be deleted permanently, do you wish to procede?"
        this.confirmation.open()
    }

    confirmationDeleteSlider() {
        this.loaderActive = true
        this.confirmation.close();
        this.sliderService.deleteSlider(this.idSliderDelete)
            .finally(() => this.isRequesting = false)
            .subscribe(
                (result) => {
                },
                errors => {
                    this.toasty.addToast("", 'An error has occurred.', "error");
                },
                () => {
                    this.getSliders()
                    this.toasty.addToast("", 'The sliders have been deleted.', "success");
                    this.loaderActive = false
                }
            )
    }

    cancelDeleteSlider() {
        this.idSliderDelete = 0;
        this.successMensaje = '';
        this.confirmation.close();
    }

    onchangueFiles(fileInput) {
        if (this.numberSliders == 5) {
            this.toasty.addToast("", 'Sorry, only five images are allowed to upload.', "info");
        } else {
            if (this.numberSliders < 5) {
                if (fileInput.target.files && fileInput.target.files[0]) {
                    this.file = fileInput.target.files;
                    for (var i = 0; i < fileInput.target.files.length; i++) {
                        this.addElements(fileInput.target.files[i]);
                    }
                }
            } else {
                this.toasty.addToast("", 'Sorry, only five images are allowed to upload.', "info");
            }
        }
    }

    addElements(fileInput) {
        const reader = new FileReader();
        reader.onload = ((e) => {
                if (fileInput.type == "image/jpeg" || fileInput.type == "image/png") {
                    var image = new Image();
                    image.src = e.target['result'];
                    image.onload = () => {
                        if (image.height < 342  || image.width < 1904) {
                            this.toasty.addToast('', 'The height and width should be greater 1904x342.', 'info')
                        } else {
                            this.arrayElementos.push({
                                archivo: e.target['result'],
                                tipo: fileInput.type,
                                nombre: fileInput.name,
                                tamano: fileInput.size,
                                descripcionElemento: "",
                                descripcionElementoAux: "",
                            });
                            this.numberSliders++;
                        }
                        (<HTMLInputElement>document.getElementById('file-upload')).value = '';
                    }
                } else {
                    this.toasty.addToast("", 'File format not allowed', "info");
                }


            }

        );
        reader
            .readAsDataURL(fileInput);
    }

    deleteElement(index) {
        this.numberSliders--;
        this.arrayElementos.splice(index, 1);
    }

    openUrlImgMap(obj){
        if(obj.rutaMaps !="null") {
            window.open(obj.rutaMaps + obj.idAtleta+"-2", '_blank', 'location=yes,height=570,width=520,scrollbars=yes,status=yes');
        }
    }

    openForm(slider ?) {
        if (slider) {
            this.sliderModel = {...slider}
            this.arrayElementos.push({
                archivo: this.sliderModel.urlArchivoOriginal
            });

            this.modalRegister.open('lg');
        } else {
            this.sliderModel.idAtleta = this.player.id;
            this.modalRegister.open('lg');
        }
    }

    closeForm() {
        this.modalRegister.close();

        setTimeout(() => {
            this.sliderModel = <Slider>{};
            if (document.getElementById('file-upload')) {
                (<HTMLInputElement>document.getElementById('file-upload')).value = '';
            }
            this.arrayElementos = [];
        }, 500)
    }

    back() {
        this.route.navigate(['admin/players'])
    }
}
