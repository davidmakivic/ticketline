import {HttpClient} from "@angular/common/http";
import {BehaviorSubject} from "rxjs";
import {Injectable} from "@angular/core";
import {Globals} from "../global/globals";
import {UserDto} from "../dtos/user";

@Injectable({providedIn: 'root'})
export class RewardService {

  private userBaseUrl: string = this.globals.backendUri + '/users';
  private pointsSubject = new BehaviorSubject<number | null>(null);
  points$ = this.pointsSubject.asObservable();

  constructor(
    private httpClient: HttpClient,
    private globals: Globals,
  ) {
  }

  loadPoints(): void {
    console.log('Loading reward points');
    this.httpClient.get<UserDto>(this.userBaseUrl + '/me').subscribe(user => {
      this.pointsSubject.next(user.rewardPoints);
    });
  }

  clear(): void {
    this.pointsSubject.next(null);
  }
}
