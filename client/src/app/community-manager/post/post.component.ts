import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {ModalComponent} from "ng2-bs4-modal/lib/components/modal";
import {FormGroup} from "@angular/forms";
import {Router} from "@angular/router";
import {Redaccion} from "../../shared/models/redaccion";
import {Categoria} from "../../shared/models/categoria";
import {PostService} from "../../shared/services/post.service";
import {IMyDpOptions} from 'mydatepicker';
import {DatePipe} from "@angular/common";
import {NotifyService} from "../../shared/services/notify.service";

@Component({
    selector: 'app-post',
    templateUrl: './post.component.html',
    styleUrls: ['./post.component.css']
})
export class PostComponent implements OnInit {
    @ViewChild('modalRegister') modalRegister: ModalComponent;
    @ViewChild('confirmation') confirmation: ModalComponent;
    @ViewChild('formPost') formPost: FormGroup;
    @ViewChild('modalDateTime') modalDateTime: ModalComponent;
    @ViewChild('modalCoverImage') modalCoverImage: ModalComponent;
    @ViewChild('videocover') videocover: ElementRef;
    @ViewChild('filePost') filePost: ElementRef;
    public successMensaje: string;
    public file: FileList;
    public arrayElementos: any;
    public arrayElementosUpdate: any;
    public arrayTipoPost: any;
    public isRequesting: boolean;
    public loaderActive: boolean = false;
    public isUpdate: boolean = false;
    public search: any;
    public player: any;
    public redaccionModel: Redaccion;
    public categoriaModel: Categoria;
    public idPostDelete: any;
    public data: any;
    public p: any;

    // Cover //
    public ramMaxValue: any = 100;
    public archivoAux: any;
    public indexAux: any;
    public startVideo: boolean = true;
    public coverAux: any = null;
    public dataURL: any = null;
    public btnclass: string = 'fa fa-play';

    // Program //
    public date: Date;
    public time: any;
    public dataPicker: any;
    public myDatePickerOptions: IMyDpOptions;
    public mensajeErrorDateTime: any;
    public isAlertDateTime: any;

    constructor(private toasty: NotifyService, private postService: PostService, private route: Router) {
        this.data = []
        this.date = new Date();
        this.myDatePickerOptions = {dateFormat: 'dd/mm/yyyy'};
        this.arrayElementos = []
        this.arrayElementosUpdate = []
        this.player = JSON.parse(localStorage.getItem('player'))
        this.redaccionModel = <Redaccion>{};
        this.redaccionModel.idCategoria = ''
        this.redaccionModel.tipoPost = ''
        this.redaccionModel.descripcion = ''
        this.redaccionModel.tituloPost = ''
        this.redaccionModel.publicar = '1'
        this.redaccionModel.fechaPublicar = ''

        if (!this.player) {
            this.route.navigate(['cm'])
        }
    }

    ngOnInit() {
        this.arrayTipoPost = [
            {id: "1", nombre: "Private post"},
            {id: "2", nombre: "Public post"},
            {id: "4", nombre: "Publicity post"}
        ];
        this.getPosts()
        this.searchCategories()
    }

    getPosts() {
        this.loaderActive = true;
        this.postService.getPosts(this.player.id)
            .finally(() => this.isRequesting = false)
            .subscribe(
                (result) => {
                    if (result.errorAPI) {
                        this.loaderActive = false;
                        this.data = []
                    } else {
                        this.loaderActive = false;

                        let dataAux = result.redacciones;
                        for (let item of dataAux) {
                            let hashtag = ""

                            if (item.tituloPost && item.tituloPost.length > 0) {
                                hashtag = this.searchTags(item.tituloPost)
                            }

                            if (item.descripcionPost && item.descripcionPost.length > 0) {
                                hashtag = hashtag + " " + this.searchTags(item.descripcionPost)
                            }

                            item.tags = this.IsAllSapce(hashtag) ? '' : hashtag
                        }
                        this.data = dataAux;

                    }
                },
                errors => {
                    this.loaderActive = false;
                }
            )
    }

    getPost(idPost) {
        this.isUpdate = true;
        this.postService.getPost(idPost).subscribe(
            (data) => {
                this.redaccionModel.id = data.redaccion.id;
                this.redaccionModel.idAtleta = data.redaccion.atleta.id;
                this.redaccionModel.idCategoria = data.redaccion.categoria.id;
                this.redaccionModel.tituloPost = data.redaccion.tituloPost;
                this.redaccionModel.descripcion = data.redaccion.descripcionPost;
                this.redaccionModel.tipoPost = data.redaccion.tipoPost;
                this.redaccionModel.publicar = data.redaccion.publicar;
                this.redaccionModel.estatus = data.redaccion.estatus;
                this.redaccionModel.linkInsignia = data.redaccion.linkInsignia;
                this.redaccionModel.idInsignia = '';
                this.redaccionModel.fechaPublicar = data.redaccion.fechaPublicacion

                let index = 0;
                for (let item of data.redaccion.media) {
                    this.arrayElementos.push({
                        id: index,
                        archivo: item.urlArchivoOriginal,
                        nombre: item.nombreArchivoOriginal,
                        descripcionElemento: item.descripcion,
                        cover: {archivo: item.urlArchivoNormal, isUpdate: true},
                        tipo: item.tipo,
                        imagenBytes: 'noBytes',
                        delete: false
                    })
                    index++;
                }

                this.arrayElementosUpdate = this.arrayElementos.map(obj => ({...obj}));
            }
        )
    }

    searchCategories() {
        this.postService.getCategorias(this.player.id)
            .finally(() => this.isRequesting = false)
            .subscribe(
                (result) => {
                    this.categoriaModel = result.categorias;
                },
                errors => {
                }
            )
    }

    searchTags(tag) {
        var cadena = tag
        var separador = " "
        var hashtag = " "
        var arrayDeCadenas = cadena.split(separador);
        for (var x = 0; x < arrayDeCadenas.length; x++) {
            if (arrayDeCadenas[x].indexOf('#') != -1) {
                hashtag = hashtag + " " + arrayDeCadenas[x]
            } else {
            }
        }
        return hashtag
    }

    IsAllSapce(hashtag) {
        for (let i = 0; i < hashtag.length; i++)
            if (hashtag.charAt(i) != ' ')
                return false;

        return true;
    }

    onchangueFiles(fileInput) {
        if (fileInput.target.files && fileInput.target.files[0]) {
            this.file = fileInput.target.files;
            for (var i = 0; i < fileInput.target.files.length; i++) {
                this.addElements(fileInput.target.files[i], fileInput);
            }
            (<HTMLInputElement>document.getElementById('file-upload')).value = '';
        }
    }

    uploadCoverImage(event) {
        let fileList: FileList = event.target.files;
        if (fileList.length > 0) {
            const reader = new FileReader();
            reader.onload = ((e) => {
                if (event.target.files[0].type == "image/jpeg" || event.target.files[0].type == "image/png") {
                    this.dataURL = e.target['result'];
                    this.coverAux = {tipo: event.target.files[0].type, cover: true, archivo: this.dataURL};
                } else {
                    this.toasty.addToast("", 'File format not allowed', "info");
                }
            });
            reader.readAsDataURL(event.target.files[0]);
        }
    }

    addElements(fileInput, evento) {
        const reader = new FileReader();
        reader.onload = ((e) => {
            if (fileInput.type == "image/jpeg" || fileInput.type == "image/png" || fileInput.type == "video/mp4") {
                if (fileInput.type == "video/mp4") {
                    var video = document.createElement('video')
                    video.src = e.target['result']

                    video.addEventListener('loadeddata', () => {
                        console.log(video.videoHeight)
                        console.log(video.videoWidth)
                        if (video.videoHeight < 720 || video.videoWidth < 1280) {
                            this.toasty.addToast('', 'The height and width should be greater 1280×720.', 'info')
                        } else {
                            var index = this.arrayElementos.push({
                                archivo: e.target['result'],
                                tipo: fileInput.type,
                                nombre: fileInput.name,
                                tamano: fileInput.size,
                                descripcionElemento: "",
                                descripcionElementoAux: "",
                                evento: evento,
                                cover: "",

                            });

                            if (fileInput.type == "video/mp4") {
                                this.addCoverImage((index - 1), true);
                            }

                            if (this.isUpdate) {
                                this.arrayElementosUpdate.push(this.arrayElementos[index - 1]);
                                this.arrayElementosUpdate[this.arrayElementosUpdate.length - 1].id = Math.random();
                                this.arrayElementosUpdate[this.arrayElementosUpdate.length - 1].imagenBytes = "";
                            }
                        }
                    }, false);

                } else if (fileInput.type == "image/jpeg" || fileInput.type == "image/png") {
                    var image = new Image();
                    image.src = e.target['result'];

                    image.onload = () => {
                        if (image.height < 593 || image.width < 593) {
                            this.toasty.addToast('', 'The height and width should be greater 593×593.', 'info')
                        } else {
                            var index = this.arrayElementos.push({
                                archivo: e.target['result'],
                                tipo: fileInput.type,
                                nombre: fileInput.name,
                                tamano: fileInput.size,
                                descripcionElemento: "",
                                descripcionElementoAux: "",
                                evento: evento,
                                cover: "",

                            });

                            if (this.isUpdate) {
                                this.arrayElementosUpdate.push(this.arrayElementos[index - 1]);
                                this.arrayElementosUpdate[this.arrayElementosUpdate.length - 1].id = Math.random();
                                this.arrayElementosUpdate[this.arrayElementosUpdate.length - 1].imagenBytes = "";
                            }
                        }
                    }
                }
            } else {
                this.toasty.addToast("", 'File format not allowed', "info");
            }
        });

        reader.readAsDataURL(fileInput);
    }

    deleteElement(index) {
        if (this.isUpdate) {
            let indexUpdate = -1;
            if (this.arrayElementos[index].id != null && this.arrayElementos[index].id != undefined) {
                let indexAux = 0;
                for (let item of this.arrayElementosUpdate) {
                    if (item.id === this.arrayElementos[index].id) {
                        if (item.delete != undefined) {
                            item.delete = true;
                        } else {
                            indexUpdate = indexAux;
                        }
                    }
                    indexAux++
                }
            }
            if (indexUpdate >= 0) {
                this.arrayElementosUpdate.splice(indexUpdate, 1);
            }
        }
        this.arrayElementos.splice(index, 1);
    }

    addCoverImage(i, init) {
        this.archivoAux = this.arrayElementos[i].archivo;
        this.indexAux = i;
        this.dataURL = null;
        if (init) {
            this.modalCoverImage.open('lg').then(() => {
                let video = this.videocover.nativeElement;
                let canvas = this.capture(video, null);
                this.arrayElementos[(this.indexAux)].cover = {
                    tipo: 'image/png',
                    cover: true,
                    archivo: canvas.toDataURL()
                };
                this.ramMaxValue = this.videocover.nativeElement.duration;
            });
        } else {
            this.modalCoverImage.open('lg');
        }
    }

    capture(video, scaleFactor) {
        if (scaleFactor == null) {
            scaleFactor = 1;
        }
        let w = video.videoWidth * scaleFactor;
        let h = video.videoHeight * scaleFactor;
        let canvas = document.createElement('canvas');
        canvas.width = w;
        canvas.height = h;
        let ctx = canvas.getContext('2d');
        ctx.drawImage(video, 0, 0, w, h);
        video.pause();
        return canvas;
    }

    playStop(action, e?) {
        if (action) {
            if (e) this.btnclass = 'fa fa-pause';
            else
                this.btnclass = 'fa fa-play';
        } else {
            if (this.btnclass == 'fa fa-play') {
                this.btnclass = 'fa fa-pause';
                this.videocover.nativeElement.play();
            } else {
                this.btnclass = 'fa fa-play';
                this.videocover.nativeElement.pause();
            }
        }
    }

    capturaFrame(event: any) {
        this.coverAux = {tipo: 'image/png', cover: true, archivo: this.shoot()};
    }

    shoot() {
        var video = this.videocover.nativeElement;
        var canvas = this.capture(video, null);
        this.dataURL = canvas.toDataURL();
        return this.dataURL;
    }

    closeModalCover() {
        this.indexAux = null;
        this.coverAux = null;
        this.archivoAux = null;
        this.startVideo = true;
        this.btnclass = 'fa fa-play'

        this.dataURL = null;
        this.modalCoverImage.close();
    }

    saveModalCover() {
        this.arrayElementos[this.indexAux].cover = this.coverAux;
        if (this.isUpdate) {
            for (let item of this.arrayElementosUpdate) {
                if (item.id == this.arrayElementos[this.indexAux].id) {
                    item.cover = this.coverAux;
                }
            }
        }
        this.indexAux = null;
        this.coverAux = null;
        this.archivoAux = null;
        this.startVideo = true;
        this.btnclass = 'fa fa-play'
        this.dataURL = null;
        this.modalCoverImage.close();
    }

    openProgram() {
        this.mensajeErrorDateTime = "";
        this.isAlertDateTime = false;
        this.time = {hour: new Date().getHours(), minute: new Date().getMinutes()};
        this.dataPicker = {
            date: {
                year: new Date().getFullYear(),
                month: new Date().getMonth() + 1,
                day: this.date.getDate()
            }
        };

        this.modalDateTime.open('sm');
    }

    closeDateTime() {
        this.modalDateTime.close();
    }

    checkDate() {
        let now = new Date();
        let program = new Date();
        program.setFullYear(this.dataPicker.date.year, this.dataPicker.date.month - 1, this.dataPicker.date.day)
        program.setHours(this.time.hour, this.time.minute, 0)

        if (now.getTime() <= program.getTime()) {
            this.isAlertDateTime = false;
        } else {
            this.isAlertDateTime = true;
            this.mensajeErrorDateTime = "the publication date is less than today";
        }
    }

    addDateTime() {
        let datePipe = new DatePipe('en-US');
        let program = new Date();
        program.setFullYear(this.dataPicker.date.year, this.dataPicker.date.month - 1, this.dataPicker.date.day)
        program.setHours(this.time.hour, this.time.minute, 0)

        this.redaccionModel.publicar = "2";
        this.redaccionModel.fechaPublicar = datePipe.transform(program, 'dd/MM/yyyy HH:mm:ss')
        this.modalDateTime.close();
    }

    removeDateTime() {
        this.redaccionModel.publicar = '1';
        this.redaccionModel.fechaPublicar = ''
    }

    savePost() {
        if (this.formPost.valid && (this.isUpdate ? this.arrayElementosUpdate.length > 0 : this.arrayElementos.length > 0)) {
            this.loaderActive = true;
            this.redaccionModel.idAtleta = this.player.id;
            this.redaccionModel.username = "";
            this.redaccionModel.username = localStorage.getItem('username');
            if (this.isUpdate) { // update
                this.postService.enviarPost(this.arrayElementosUpdate.length, this.redaccionModel, [], 0, this.arrayElementosUpdate)
                    .finally(() => this.isRequesting = false)
                    .subscribe(
                        (result) => {
                            if (result.errorAPI) {
                                setTimeout(() => {
                                    this.loaderActive = false;
                                    this.toasty.addToast("", 'An error has occurred', "error");
                                }, 3000);
                            } else {
                                setTimeout(() => {
                                    this.loaderActive = false;
                                    this.toasty.addToast("", 'The post have been saved.', "success");
                                    this.getPosts()
                                    this.closeForm();
                                }, 3000);
                            }
                        },
                        errors => {
                            setTimeout(() => {
                                this.loaderActive = false;
                                this.toasty.addToast("", 'An error has occurred', "error");
                            }, 3000);
                        }
                    )
            } else {// new
                this.postService.enviarPost(this.arrayElementos.length, this.redaccionModel, [], 0, this.arrayElementos)
                    .finally(() => this.isRequesting = false)
                    .subscribe(
                        (result) => {
                            if (result.errorAPI) {
                                setTimeout(() => {
                                    this.loaderActive = false;
                                    this.toasty.addToast("", 'An error has occurred', "error");
                                }, 3000);
                            } else {
                                setTimeout(() => {
                                    this.loaderActive = false;
                                    this.toasty.addToast("", 'The post have been saved.', "success");
                                    this.getPosts()
                                    this.closeForm();
                                }, 3000);
                            }
                        },
                        errors => {
                            setTimeout(() => {
                                this.loaderActive = false;
                                this.toasty.addToast("", 'An error has occurred', "error");
                            }, 3000);
                        }
                    )
            }
        } else {
            for (let i in this.formPost.controls)
                this.formPost.controls[i].markAsTouched();

            if (this.formPost.valid) {
                this.toasty.addToast("", 'A file is necessary', "error");
            } else {
                this.toasty.addToast("", 'Some required fields are missing', "error");
            }
        }
    }

    statusPost() {
        this.redaccionModel.estatus = this.redaccionModel.estatus == '0' ? '1' : '0'
        this.loaderActive = true
        this.postService.updateStatusPost(this.redaccionModel.id, this.redaccionModel.estatus)
            .finally(() => this.isRequesting = false)
            .subscribe(
                (result) => {
                    this.loaderActive = false
                    this.getPosts();
                }
            )
    }

    openDeletePost(post) {
        this.idPostDelete = post.id;
        this.successMensaje = "The post and the elements that contains (photos or videos) will be deleted permanently, do you wish to procede?"
        this.confirmation.open()
    }

    confirmationDeletePost() {
        this.confirmation.close();
        this.postService.deletePost(this.idPostDelete)
            .finally(() => this.isRequesting = false)
            .subscribe(
                (result) => {
                    this.toasty.addToast("", 'The post have been deleted.', "success");
                    this.getPosts();
                },
                errors => {
                    this.toasty.addToast("", 'An error has occurred', "error");
                }
            )
    }

    cancelDeletePost() {
        this.idPostDelete = 0;
        this.confirmation.close();
    }


    openForm(post?) {
        if (post) {
            this.getPost(post.id)
            this.modalRegister.open()
        } else {
            this.modalRegister.open()
        }
    }

    openUrlImgMap(obj){
        if(obj.rutaMaps !="null") {
            window.open(obj.rutaMaps + obj.id+"-1", '_blank', 'location=yes,height=570,width=520,scrollbars=yes,status=yes');
        }
    }

    closeForm() {
        this.modalRegister.close();
        this.formPost.reset()
        setTimeout(() => {
            this.arrayElementos = []
            this.arrayElementosUpdate = []
            this.redaccionModel = <Redaccion>{};
            this.redaccionModel.idCategoria = ''
            this.redaccionModel.tipoPost = ''
            this.redaccionModel.descripcion = ''
            this.redaccionModel.tituloPost = ''
            this.redaccionModel.publicar = '1'
            this.redaccionModel.fechaPublicar = ''
        }, 600)
    }

    goCalendar() {
        this.route.navigate(['cm/calendar'])
    }

    back() {
        this.route.navigate(['cm'])
    }
}
