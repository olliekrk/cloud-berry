import {BrowserModule} from "@angular/platform-browser";
import {NgModule} from "@angular/core";

import {AppComponent} from "./app.component";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {AppRoutingModule} from "./app-routing.module";
import {RouterModule} from "@angular/router";
import {MainDashboardComponent} from "./component/main-dashboard/main-dashboard.component";
import {LayoutModule} from "@angular/cdk/layout";
import {MatToolbarModule} from "@angular/material/toolbar";
import {MatButtonModule} from "@angular/material/button";
import {MatSidenavModule} from "@angular/material/sidenav";
import {MatIconModule} from "@angular/material/icon";
import {MatListModule} from "@angular/material/list";
import {MatGridListModule} from "@angular/material/grid-list";
import {MatCardModule} from "@angular/material/card";
import {MatMenuModule} from "@angular/material/menu";
import {ConfigurationDashboardComponent} from "./component/configuration-dashboard/configuration-dashboard.component";
import {NavigationToolbarComponent} from "./component/navigation-toolbar/navigation-toolbar.component";
import {LoggerConfig, LoggerModule} from "ngx-logger";
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import {TopologyDashboardComponent} from "./component/topology-dashboard/topology-dashboard.component";
import {TopologyGraphComponent} from "./component/topology-graph/topology-graph.component";
import {TopologyNodeDetailsInfoDialogComponent} from "./component/node-info-dialog/topology-node-details-info-dialog.component";
import {MatDialogModule} from "@angular/material/dialog";
import {FlexLayoutModule} from "@angular/flex-layout";
import {DefaultHttpHeadersInterceptor} from "./interceptor/default-http-headers-interceptor";
import {MatProgressSpinnerModule} from "@angular/material/progress-spinner";
import {MatTooltipModule} from "@angular/material/tooltip";
import {TopologyCreateDialogComponent} from "./component/topology-create-dialog/topology-create-dialog.component";
import {ReactiveFormsModule} from "@angular/forms";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatInputModule} from "@angular/material/input";
import {TopActionBarComponent} from "./component/top-action-bar/top-action-bar.component";
import {MatOptionModule} from "@angular/material/core";
import {MatSelectModule} from "@angular/material/select";

@NgModule({
  declarations: [
    AppComponent,
    MainDashboardComponent,
    TopologyNodeDetailsInfoDialogComponent,
    TopologyDashboardComponent,
    ConfigurationDashboardComponent,
    NavigationToolbarComponent,
    TopologyGraphComponent,
    TopologyCreateDialogComponent,
    TopActionBarComponent,
  ],
    imports: [
        BrowserModule,
        BrowserAnimationsModule,
        AppRoutingModule,
        RouterModule,
        LayoutModule,
        MatToolbarModule,
        MatButtonModule,
        MatSidenavModule,
        MatIconModule,
        MatListModule,
        MatGridListModule,
        MatCardModule,
        MatMenuModule,
        LoggerModule,
        HttpClientModule,
        MatTooltipModule,
        MatSelectModule,
        MatDialogModule,
        MatFormFieldModule,
        MatInputModule,
        FlexLayoutModule,
        MatProgressSpinnerModule,
        ReactiveFormsModule,
        MatOptionModule
    ],
  providers: [
    LoggerConfig,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: DefaultHttpHeadersInterceptor,
      multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
