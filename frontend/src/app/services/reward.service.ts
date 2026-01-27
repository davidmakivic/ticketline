import {HttpClient} from "@angular/common/http";
import {BehaviorSubject, Observable} from "rxjs";
import {Injectable} from "@angular/core";
import {Globals} from "../global/globals";
import {UserDto} from "../dtos/user";
import {RewardDto} from "../dtos/reward";

@Injectable({providedIn: 'root'})
export class RewardService {

  private userBaseUrl: string = this.globals.backendUri + '/users';
  private rewardsBaseUrl = this.globals.backendUri + '/rewards'
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

  getCurrentPoints(): number {
    return this.pointsSubject.value;
  }

  getAll(): Observable<RewardDto[]> {
    return this.httpClient.get<RewardDto[]>(this.rewardsBaseUrl);
  }

  decreasePoints(number: number) {
    const current = this.pointsSubject.value ?? 0;
    if (current < number) {
      throw new Error('Not enough reward points');
    }
    this.pointsSubject.next(current - number);
  }
}
