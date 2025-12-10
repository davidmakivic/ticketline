import { Component } from '@angular/core';
import {MatButtonToggle, MatButtonToggleGroup} from "@angular/material/button-toggle";
import {FormsModule} from "@angular/forms";

@Component({
  selector: 'app-top10',
  imports: [
    MatButtonToggleGroup,
    MatButtonToggle,
    FormsModule
  ],
  templateUrl: './top10.component.html',
  styleUrl: './top10.component.scss',
})
export class Top10Component {

  selectedCategory = "ALL";
}
