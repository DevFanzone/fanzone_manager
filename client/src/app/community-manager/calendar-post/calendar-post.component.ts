import {Component, OnInit, ViewChild} from '@angular/core';
import {PostService} from "../../shared/services/post.service";
import {Router} from "@angular/router";
import {ModalComponent} from "ng2-bs4-modal/lib/components/modal";
import {IMyDpOptions} from 'mydatepicker';
import {DatePipe} from "@angular/common";
import {CalendarComponent} from "ng-fullcalendar";
import {NotifyService} from "../../shared/services/notify.service";

@Component({
    selector: 'app-calendar-post',
    templateUrl: './calendar-post.component.html',
    styleUrls: ['./calendar-post.component.css']
})
export class CalendarPostComponent implements OnInit {
    @ViewChild('modalDateTime') modalDateTime: ModalComponent;
    @ViewChild('modalConfirmation') modalConfirmation: ModalComponent;
    @ViewChild('ucCalendar') ucCalendar: CalendarComponent;

    public player: any;
    public isRequesting: boolean;
    public loaderActive: boolean = false;
    public data: any;
    public displayEvent: any;
    public options: Object;

    public time: any;
    public dataPicker: any;
    public myDatePickerOptions: IMyDpOptions;
    public mensajeErrorDateTime: any;
    public isAlertDateTime: any;
    public fechaModificada: string;
    public idPostfechaModificada: any;

    constructor(private toasty: NotifyService, private postService: PostService, private route: Router) {
        this.myDatePickerOptions = {dateFormat: 'dd/mm/yyyy'};
    }

    ngOnInit() {
        this.player = JSON.parse(localStorage.getItem('player'))
        if (!this.player) {
            this.route.navigate(['cm'])
        }

        this.getPosts()
    }

    getPosts() {
        this.loaderActive = true;
        this.postService.getPosts(this.player.id)
            .finally(() => this.isRequesting = false)
            .subscribe(
                (result) => {
                    if (result.errorAPI) {
                        this.loaderActive = false;
                        this.prepararCalendar([])
                    } else {
                        this.loaderActive = false;
                        this.prepararCalendar(result.redacciones)
                    }
                },
                errors => {
                    this.loaderActive = false;
                }
            )
    }

    prepararCalendar(posts) {
        let dataModel: any = [];
        this.data = []

        for (let post of posts) {
            let fechaCalendar = ""
            if (post.publicar == "2" && post.estatus == "0") {
                this.data.push(post)
                fechaCalendar = this.parsearFechaToCalendar(post.fechaPublicacion)

                dataModel = [...dataModel, {
                    id: post.id,
                    title: post.nombreAtleta,
                    start: fechaCalendar,
                    textColor: '#FFFFFF',
                    backgroundColor: '#69b6e4',
                    borderColor: '#69b6e4',
                }];


            }
        }

        this.options = {
            editable: false,
            eventLimit: false,
            header: {
                left: 'prev,next today',
                center: 'title',
                right: 'month,agendaWeek'
            },
            events: dataModel
        };

        if (this.ucCalendar) {
            this.ucCalendar.renderEvents(dataModel);
        }
    }

    parsearFechaToCalendar(fechaPost) {
        let fecharReturn = "", ano = "", mes = "", dia = "", hh = "", mm = "", sg = "";
        let separadorFechaHora = " ", arregloDeSubCadenas = fechaPost.split(separadorFechaHora);
        let separadorHora = ":", arregloDeSubCadenasHora = arregloDeSubCadenas[1].split(separadorHora);
        let separadorFechaPost = "/", arregloDeSubCadenasFechaPost = arregloDeSubCadenas[0].split(separadorFechaPost);

        dia = arregloDeSubCadenasFechaPost[0];
        mes = arregloDeSubCadenasFechaPost[1];
        hh = arregloDeSubCadenasHora[0];
        mm = arregloDeSubCadenasHora[1];

        fecharReturn = arregloDeSubCadenasFechaPost[2] + "-" + mes + "-" + dia + "T" + hh + ":" + mm + ":" + "00";
        return fecharReturn;
    }


    clickButton(model: any) {
        this.displayEvent = model;
    }

    eventClick(model: any) {
        model = {
            event: {
                id: model.event.id,
                start: model.event.start,
                end: model.event.end,
                title: model.event.title,
                allDay: model.event.allDay
            },
            duration: {}
        }
        this.displayEvent = model;

        for (let post of this.data) {
            if (post.id == model.event.id) {
                this.editarPostFullCalendar(model.event.id, post.fechaPublicacion);
            }
        }
    }

    updateEvent(model: any) {
        model = {
            event: {
                id: model.event.id,
                start: model.event.start,
                end: model.event.end,
                title: model.event.title
            },
            duration: {
                _data: model.duration._data
            }
        }
        this.displayEvent = model;
    }

    editarPostFullCalendar(idPost, fechaPost) {
        this.fechaModificada = "";
        this.idPostfechaModificada = idPost;
        this.mensajeErrorDateTime = "";
        this.isAlertDateTime = false;

        let separadorFechaHora = " ", arregloDeSubCadenas = fechaPost.split(separadorFechaHora);
        let separadorHora = ":", arregloDeSubCadenasHora = arregloDeSubCadenas[1].split(separadorHora);
        let separadorFechaPost = "/", arregloDeSubCadenasFechaPost = arregloDeSubCadenas[0].split(separadorFechaPost);
        let year = parseInt(arregloDeSubCadenasFechaPost[2])
        let month = parseInt(arregloDeSubCadenasFechaPost[1])
        let day = parseInt(arregloDeSubCadenasFechaPost[0])

        this.dataPicker = {
            date: {
                year: year,
                month: month,
                day: day
            }
        };
        this.time = {hour: arregloDeSubCadenasHora[0], minute: arregloDeSubCadenasHora[1]};
        this.modalDateTime.open();

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

        this.fechaModificada = datePipe.transform(program, 'dd/MM/yyyy HH:mm:ss')
        this.modalDateTime.close();
        this.modalConfirmation.open();

    }

    updateDatePost() {
        this.loaderActive = true;
        this.postService.updateDatePost(this.idPostfechaModificada, this.fechaModificada)
            .finally(() => this.isRequesting = false)
            .subscribe(
                (result) => {
                    setTimeout(() => {
                        this.loaderActive = false;
                        this.toasty.addToast("", 'The post have been modified.', "success");
                        this.getPosts();
                    }, 500)
                },
                errors => {
                    this.loaderActive = false;
                    this.toasty.addToast("", 'An error has occurred', "error");
                }
            )
        this.modalConfirmation.close();
    }

    cancelUpdate() {
        this.idPostfechaModificada = ''
        this.fechaModificada = ''
        this.modalConfirmation.close();
    }

    back() {
        this.route.navigate(['cm/post'])
    }
}
