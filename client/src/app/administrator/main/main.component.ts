import { Component, OnInit } from '@angular/core';
import {Router} from "@angular/router";

@Component({
  selector: 'app-main',
  templateUrl: './main.component.html',
  styleUrls: ['./main.component.css']
})
export class MainComponent implements OnInit {

  constructor(private route: Router) { }

  ngOnInit() {
  }

  goToCm() {
    this.route.navigate(['/admin/cm'])
  }

  goToPlayers() {
    this.route.navigate(['/admin/players'])
  }

  goToUser() {
    this.route.navigate(['/admin/users'])
  }

}
